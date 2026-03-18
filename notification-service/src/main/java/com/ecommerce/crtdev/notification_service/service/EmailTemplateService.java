package com.ecommerce.crtdev.notification_service.service;

import org.springframework.stereotype.Component;

@Component
public class EmailTemplateService {

    private static final String BRAND_COLOR  = "#4F46E5";
    private static final String SUCCESS_COLOR = "#059669";
    private static final String DANGER_COLOR  = "#DC2626";
    private static final String WARNING_COLOR = "#D97706";

    public String orderConfirmed(String orderId) {
        return wrap(
                SUCCESS_COLOR,
                "Order confirmed",
                "Your payment was processed successfully.",
                orderId,
                """
                <p style="margin:0 0 16px;color:#374151;">
                  Great news! Your order has been confirmed and is now being prepared.
                  You'll receive updates as your order progresses.
                </p>
                """,
                "View your order",
                SUCCESS_COLOR
        );
    }

    public String orderCancelledByUser(String orderId) {
        return wrap(
                WARNING_COLOR,
                "Order cancelled",
                "Your order has been cancelled as requested.",
                orderId,
                """
                <p style="margin:0 0 16px;color:#374151;">
                  Your order has been successfully cancelled. No charges were made.
                  If you change your mind, you can place a new order anytime.
                </p>
                """,
                "Continue shopping",
                BRAND_COLOR
        );
    }

    public String orderCancelledPaymentFailed(String orderId, String reason) {
        String reasonHtml = reason != null && !reason.isBlank()
                ? "<p style=\"margin:8px 0 0;color:#6B7280;font-size:13px;\">Reason: " + escapeHtml(reason) + "</p>"
                : "";

        return wrap(
                DANGER_COLOR,
                "Payment failed",
                "We couldn't process your payment.",
                orderId,
                """
                <p style="margin:0 0 16px;color:#374151;">
                  Unfortunately your payment could not be processed and your order
                  has been cancelled. Please verify your payment details and try again.
                </p>
                """ + reasonHtml,
                "Try again",
                BRAND_COLOR
        );
    }

    public String orderRefunded(String orderId) {
        return wrap(
                BRAND_COLOR,
                "Refund processed",
                "Your refund has been successfully processed.",
                orderId,
                """
                <p style="margin:0 0 16px;color:#374151;">
                  Your refund has been processed. Depending on your bank,
                  funds typically appear within 3–5 business days.
                </p>
                """,
                "View order details",
                BRAND_COLOR
        );
    }

    private String wrap(String accentColor, String title, String subtitle,
                        String orderId, String body, String ctaText, String ctaColor) {
        return """
            <!DOCTYPE html>
            <html lang="en">
            <head>
              <meta charset="UTF-8"/>
              <meta name="viewport" content="width=device-width,initial-scale=1"/>
            </head>
            <body style="margin:0;padding:0;background:#F9FAFB;font-family:-apple-system,BlinkMacSystemFont,'Segoe UI',Roboto,sans-serif;">
              <table width="100%%" cellpadding="0" cellspacing="0" style="background:#F9FAFB;padding:40px 20px;">
                <tr><td align="center">
                  <table width="100%%" cellpadding="0" cellspacing="0" style="max-width:560px;">
 
                    <tr>
                      <td style="background:%s;border-radius:8px 8px 0 0;padding:32px 40px;text-align:center;">
                        <h1 style="margin:0;color:#ffffff;font-size:22px;font-weight:600;">%s</h1>
                        <p style="margin:8px 0 0;color:rgba(255,255,255,0.85);font-size:14px;">%s</p>
                      </td>
                    </tr>
 
                    <tr>
                      <td style="background:#ffffff;padding:32px 40px;">
                        %s
                        <div style="background:#F3F4F6;border-radius:6px;padding:12px 16px;margin:0 0 24px;">
                          <p style="margin:0;color:#6B7280;font-size:12px;text-transform:uppercase;letter-spacing:0.05em;">Order ID</p>
                          <p style="margin:4px 0 0;color:#111827;font-size:14px;font-family:monospace;">%s</p>
                        </div>
                        <table width="100%%" cellpadding="0" cellspacing="0">
                          <tr>
                            <td align="center">
                              <a href="#" style="display:inline-block;background:%s;color:#ffffff;text-decoration:none;padding:12px 28px;border-radius:6px;font-size:14px;font-weight:500;">%s</a>
                            </td>
                          </tr>
                        </table>
                      </td>
                    </tr>
 
                    <tr>
                      <td style="background:#F3F4F6;border-radius:0 0 8px 8px;padding:20px 40px;text-align:center;">
                        <p style="margin:0;color:#9CA3AF;font-size:12px;">
                          This email was sent by eCommerce Platform.
                        </p>
                      </td>
                    </tr>
 
                  </table>
                </td></tr>
              </table>
            </body>
            </html>
            """.formatted(accentColor, title, subtitle, body, orderId, ctaColor, ctaText);
    }

    private String escapeHtml(String text) {
        return text
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}
