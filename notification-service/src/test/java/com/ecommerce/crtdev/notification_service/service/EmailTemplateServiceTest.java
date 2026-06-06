package com.ecommerce.crtdev.notification_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class EmailTemplateServiceTest {
    private EmailTemplateService templateService;

    @BeforeEach
    void setUp(){
        templateService = new EmailTemplateService();
    }

    @Nested
    @DisplayName("orderConfirmed()")
    class OrderConfirmed {
        @Test
        @DisplayName("contains the orderId so the customer can reference it")
        void containsOrderId() {
            String html = templateService.orderConfirmed("ORD-001");
            assertThat(html).contains("ORD-001");
        }

        @Test
        @DisplayName("uses the success color to signal a positive outcome")
        void usesSuccessColor() {
            String html = templateService.orderConfirmed("ORD-001");
            assertThat(html).contains("#059669");
        }

        @Test
        @DisplayName("produces valid HTML structure")
        void producesHtmlStructure() {
            String html = templateService.orderConfirmed("ORD-001");
            assertThat(html)
                    .contains("<!DOCTYPE html>")
                    .contains("</html>");
        }
    }

    @Nested
    @DisplayName("orderCancelledByUser()")
    class OrderCancelledByUser {

        @Test
        @DisplayName("contains the orderId")
        void containsOrderId() {
            String html = templateService.orderCancelledByUser("ORD-002");
            assertThat(html).contains("ORD-002");
        }

        @Test
        @DisplayName("uses the warning color to signal a neutral/cautionary outcome")
        void usesWarningColor() {
            String html = templateService.orderCancelledByUser("ORD-002");
            assertThat(html).contains("#D97706");
        }
    }

    @Nested
    @DisplayName("orderCancelledPaymentFailed()")
    class OrderCancelledPaymentFailed {

        @Test
        @DisplayName("contains the orderId")
        void containsOrderId() {
            String html = templateService.orderCancelledPaymentFailed("ORD-003", "Insufficient funds");
            assertThat(html).contains("ORD-003");
        }

        @Test
        @DisplayName("uses the danger color to signal a failure")
        void usesDangerColor() {
            String html = templateService.orderCancelledPaymentFailed("ORD-003", "Insufficient funds");
            assertThat(html).contains("#DC2626");
        }

        @Test
        @DisplayName("includes the failure reason so the customer understands what happened")
        void includesReason() {
            String html = templateService.orderCancelledPaymentFailed("ORD-003", "Insufficient funds");
            assertThat(html).contains("Insufficient funds");
        }

        @Test
        @DisplayName("escapes < and > in the reason to prevent HTML injection")
        void escapesHtmlTagsInReason() {
            String html = templateService.orderCancelledPaymentFailed(
                    "ORD-003", "<script>alert('xss')</script>");

            assertThat(html)
                    .contains("&lt;script&gt;")
                    .doesNotContain("<script>");
        }

        @Test
        @DisplayName("escapes & in the reason")
        void escapesAmpersandInReason() {
            String html = templateService.orderCancelledPaymentFailed(
                    "ORD-003", "Visa & Mastercard declined");

            assertThat(html).contains("&amp;");
        }

        @Test
        @DisplayName("escapes double quotes in the reason")
        void escapesQuotesInReason() {
            String html = templateService.orderCancelledPaymentFailed(
                    "ORD-003", "Card \"expired\"");

            assertThat(html).contains("&quot;");
        }

        @Test
        @DisplayName("null reason does not throw and omits the reason block")
        void nullReasonDoesNotThrow() {
            // reason is optional — a null should not crash the service
            assertThat(templateService.orderCancelledPaymentFailed("ORD-003", null))
                    .contains("ORD-003")
                    .doesNotContain("Reason:");
        }

        @Test
        @DisplayName("blank reason does not throw and omits the reason block")
        void blankReasonDoesNotThrow() {
            assertThat(templateService.orderCancelledPaymentFailed("ORD-003", "   "))
                    .contains("ORD-003")
                    .doesNotContain("Reason:");
        }
    }

    @Nested
    @DisplayName("orderRefunded()")
    class OrderRefunded {

        @Test
        @DisplayName("contains the orderId")
        void containsOrderId() {
            String html = templateService.orderRefunded("ORD-004");
            assertThat(html).contains("ORD-004");
        }

        @Test
        @DisplayName("uses the brand color to signal an informational/positive outcome")
        void usesBrandColor() {
            String html = templateService.orderRefunded("ORD-004");
            assertThat(html).contains("#4F46E5");
        }
    }

}
