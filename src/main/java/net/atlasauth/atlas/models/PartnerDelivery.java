package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * One delivery a partner platform made to the receiver. {@code outcome} is recorded, unchanged, no_subject,
 * no_user, source_not_allowed or ignored.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record PartnerDelivery(
        String object,
        String eventId,
        String eventType,
        String outcome,
        String userId,
        long receivedAt) {
}
