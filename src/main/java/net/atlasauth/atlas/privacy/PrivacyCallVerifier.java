package net.atlasauth.atlas.privacy;

import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Traceparent;
import net.atlasauth.atlas.webhook.WebhookVerification;
import net.atlasauth.atlas.webhook.WebhookVerifier;

import java.util.Map;

/**
 * Verifies a call Atlas makes to a data-rights receiver. With {@code sign_requests} on, case calls carry
 * {@code Atlas-Id}, {@code Atlas-Timestamp} and {@code Atlas-Signature} exactly like a webhook (HMAC-SHA256
 * over {@code id.timestamp.body} with the signing secret); the completion endpoint's {@code privacy_request.*}
 * events are ordinary signed webhooks. Verify the RAW body.
 *
 * <pre>{@code
 * PrivacyCall call = new PrivacyCallVerifier(secret).verify(rawBody, headers);
 * if (call == null) return 400;
 * }</pre>
 */
public final class PrivacyCallVerifier {
    private final WebhookVerifier webhook;

    public PrivacyCallVerifier(String secret) {
        this.webhook = new WebhookVerifier(secret);
    }

    public PrivacyCallVerifier(String secret, long toleranceMs) {
        this.webhook = new WebhookVerifier(secret, toleranceMs);
    }

    /** The verified call, or null when the signature, timestamp or headers do not check out. */
    public PrivacyCall verify(byte[] rawBody, Map<String, ?> headers) {
        return verify(rawBody, headers, System.currentTimeMillis());
    }

    /** {@link #verify(byte[], Map)} against {@code nowMs} (epoch ms). */
    public PrivacyCall verify(byte[] rawBody, Map<String, ?> headers, long nowMs) {
        WebhookVerification v = webhook.verify(rawBody, headers, nowMs);
        if (!v.verified()) {
            return null;
        }
        JsonNode event = v.event();
        JsonNode type = event == null ? null : event.get("type");
        boolean isEvent = type != null && type.isTextual() && !type.asText().isEmpty();
        return new PrivacyCall(v.deliveryId(), v.timestamp(), Traceparent.fromHeaders(headers), event, isEvent ? event : null);
    }
}
