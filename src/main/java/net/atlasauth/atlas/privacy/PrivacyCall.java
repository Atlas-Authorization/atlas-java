package net.atlasauth.atlas.privacy;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * A verified call from Atlas to your data-rights orchestrator or executor receiver: the signed case request
 * (when Atlas opens a case for a scheduled deletion), its cancellation, or a {@code privacy_request.*} event.
 *
 * @param deliveryId  the signed {@code Atlas-Id} header (also the {@code Idempotency-Key} Atlas sends); deduplicate retries on it
 * @param timestamp   the signed {@code Atlas-Timestamp} (epoch ms)
 * @param traceparent the W3C {@code traceparent} Atlas sent, or null
 * @param body        the parsed JSON body (null when it is not a JSON object)
 * @param event       the webhook envelope when the body is one ({@code privacy_request.*}), else null
 */
public record PrivacyCall(String deliveryId, long timestamp, String traceparent, JsonNode body, JsonNode event) {
}
