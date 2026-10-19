package net.atlasauth.atlas.webhook;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * The outcome of {@link WebhookVerifier#verify}. Never thrown: check {@link #verified()}.
 *
 * @param verified true only when the signature and timestamp both check out
 * @param reason why it failed: {@code missing_headers}, {@code malformed_signature},
 *     {@code stale_timestamp} or {@code signature_mismatch}; null when verified
 * @param event the parsed envelope {@code {id, type, timestamp, instance_id, data}}; null unless verified
 * @param deliveryId the signed {@code atlas-id} header, the same on every retry of one event
 *     (deduplicate on it); null unless verified
 * @param timestamp the signed {@code atlas-timestamp} header (epoch ms); null unless verified
 */
public record WebhookVerification(
        boolean verified, String reason, JsonNode event, String deliveryId, Long timestamp) {

    static WebhookVerification failure(String reason) {
        return new WebhookVerification(false, reason, null, null, null);
    }
}
