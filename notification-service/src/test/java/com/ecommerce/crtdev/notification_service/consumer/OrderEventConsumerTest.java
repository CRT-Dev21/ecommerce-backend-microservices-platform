package com.ecommerce.crtdev.notification_service.consumer;

import com.ecommerce.crtdev.notification_service.service.EmailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderEventConsumerTest {

    @Mock
    private EmailService emailService;

    private OrderEventConsumer consumer;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        consumer = new OrderEventConsumer(emailService, objectMapper);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Helpers — build realistic JSON payloads the same way the order-service
    // would publish them. This is important: if the payload shape changes,
    // the test breaks and you catch the contract mismatch before production.
    // ─────────────────────────────────────────────────────────────────────────

    private String orderConfirmedMessage(String orderId, String email) {
        return """
            {
              "metadata": {
                "eventId": "%s",
                "eventType": "OrderConfirmed",
                "schemaVersion": "1.0",
                "source": "order-service",
                "timestamp": "%s",
                "correlationId": "%s"
              },
              "payload": {
                "orderId": "%s",
                "buyerEmail": "%s",
                "items": [
                  { "productId": "PROD-1", "sellerId": 10, "quantity": 2 }
                ]
              }
            }
            """.formatted(UUID.randomUUID(), Instant.now(), UUID.randomUUID(), orderId, email);
    }

    private String orderCancelledMessage(String orderId, String email, String reason) {
        String reasonField = reason != null
                ? "\"reason\": \"%s\"".formatted(reason)
                : "\"reason\": null";
        return """
            {
              "metadata": {
                "eventId": "%s",
                "eventType": "OrderCancelled",
                "schemaVersion": "1.0",
                "source": "order-service",
                "timestamp": "%s",
                "correlationId": "%s"
              },
              "payload": {
                "orderId": "%s",
                "buyerEmail": "%s",
                %s
              }
            }
            """.formatted(UUID.randomUUID(), Instant.now(), UUID.randomUUID(), orderId, email, reasonField);
    }

    private String unknownEventMessage() {
        return """
            {
              "metadata": {
                "eventId": "%s",
                "eventType": "OrderShipped",
                "schemaVersion": "1.0",
                "source": "order-service",
                "timestamp": "%s",
                "correlationId": "%s"
              },
              "payload": {}
            }
            """.formatted(UUID.randomUUID(), Instant.now(), UUID.randomUUID());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // OrderConfirmed routing
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("OrderConfirmed event")
    class OrderConfirmedRouting {

        @Test
        @DisplayName("routes to sendOrderConfirmed with correct orderId and email")
        void routesToSendOrderConfirmed() {
            consumer.onOrderEvent(orderConfirmedMessage("ORD-001", "buyer@example.com"));

            verify(emailService).sendOrderConfirmed("buyer@example.com", "ORD-001");
        }

        @Test
        @DisplayName("does not call any other email method")
        void doesNotCallOtherMethods() {
            consumer.onOrderEvent(orderConfirmedMessage("ORD-001", "buyer@example.com"));

            verify(emailService, never()).sendOrderCancelledByUser(any(), any());
            verify(emailService, never()).sendOrderCancelledPaymentFailed(any(), any(), any());
            verify(emailService, never()).sendOrderRefunded(any(), any());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // OrderCancelled routing — this is where the business logic lives
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("OrderCancelled event — cancellation routing")
    class CancellationRouting {

        @Test
        @DisplayName("reason 'Refunded' → sendOrderRefunded (case-insensitive)")
        void refundedReasonRoutesToSendOrderRefunded() {
            consumer.onOrderEvent(orderCancelledMessage("ORD-002", "buyer@example.com", "Refunded"));

            verify(emailService).sendOrderRefunded("buyer@example.com", "ORD-002");
        }

        @Test
        @DisplayName("reason 'refunded' (lowercase) → sendOrderRefunded (equalsIgnoreCase)")
        void refundedReasonIsCaseInsensitive() {
            // The constant uses equalsIgnoreCase — verify that contract is honored
            consumer.onOrderEvent(orderCancelledMessage("ORD-002", "buyer@example.com", "refunded"));

            verify(emailService).sendOrderRefunded("buyer@example.com", "ORD-002");
        }

        @Test
        @DisplayName("reason starting with 'Payment' → sendOrderCancelledPaymentFailed")
        void paymentReasonRoutesToPaymentFailed() {
            consumer.onOrderEvent(orderCancelledMessage("ORD-003", "buyer@example.com", "Payment declined"));

            verify(emailService).sendOrderCancelledPaymentFailed(
                    "buyer@example.com", "ORD-003", "Payment declined");
        }

        @Test
        @DisplayName("reason 'Payment' (exact prefix match) → sendOrderCancelledPaymentFailed")
        void exactPaymentPrefixRoutesToPaymentFailed() {
            consumer.onOrderEvent(orderCancelledMessage("ORD-003", "buyer@example.com", "Payment"));

            verify(emailService).sendOrderCancelledPaymentFailed(
                    "buyer@example.com", "ORD-003", "Payment");
        }

        @Test
        @DisplayName("reason 'payment' (lowercase) does NOT match Payment prefix — goes to cancelledByUser")
        void lowercasePaymentDoesNotMatchPrefix() {
            // startsWith() is case-sensitive — 'payment' != 'Payment'
            // This test documents a known behavior of the current implementation.
            // If this should be case-insensitive, the implementation needs to change.
            consumer.onOrderEvent(orderCancelledMessage("ORD-003", "buyer@example.com", "payment declined"));

            verify(emailService).sendOrderCancelledByUser("buyer@example.com", "ORD-003");
            verify(emailService, never()).sendOrderCancelledPaymentFailed(any(), any(), any());
        }

        @Test
        @DisplayName("any other reason → sendOrderCancelledByUser")
        void otherReasonRoutesToCancelledByUser() {
            consumer.onOrderEvent(orderCancelledMessage("ORD-004", "buyer@example.com", "Customer request"));

            verify(emailService).sendOrderCancelledByUser("buyer@example.com", "ORD-004");
        }

        @Test
        @DisplayName("null reason → sendOrderCancelledByUser (default branch)")
        void nullReasonRoutesToCancelledByUser() {
            // null reason should fall to the else branch without NullPointerException
            consumer.onOrderEvent(orderCancelledMessage("ORD-004", "buyer@example.com", null));

            verify(emailService).sendOrderCancelledByUser("buyer@example.com", "ORD-004");
        }

        @Test
        @DisplayName("empty reason → sendOrderCancelledByUser (default branch)")
        void emptyReasonRoutesToCancelledByUser() {
            consumer.onOrderEvent(orderCancelledMessage("ORD-004", "buyer@example.com", ""));

            verify(emailService).sendOrderCancelledByUser("buyer@example.com", "ORD-004");
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Unknown event types
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("Unknown event types")
    class UnknownEvents {

        @Test
        @DisplayName("unknown eventType is silently ignored — no email sent")
        void unknownEventTypeIsIgnored() {
            consumer.onOrderEvent(unknownEventMessage());

            verifyNoInteractions(emailService);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Malformed messages
    // ─────────────────────────────────────────────────────────────────────────
    @Nested
    @DisplayName("Malformed messages")
    class MalformedMessages {

        @Test
        @DisplayName("invalid JSON throws RuntimeException — does not silently swallow")
        void invalidJsonThrowsRuntimeException() {
            // Why test this? If the consumer silently swallows bad messages,
            // Kafka commits the offset and the message is lost forever.
            // By throwing, the consumer lets Spring Kafka's error handler decide:
            // retry, send to DLQ, or alert. Silent swallowing is worse than failing.
            org.junit.jupiter.api.Assertions.assertThrows(
                    RuntimeException.class,
                    () -> consumer.onOrderEvent("this is not json")
            );
        }

        @Test
        @DisplayName("invalid JSON does not send any email")
        void invalidJsonDoesNotSendEmail() {
            try {
                consumer.onOrderEvent("{ broken json }");
            } catch (RuntimeException ignored) {
            }
            verifyNoInteractions(emailService);
        }
    }
}