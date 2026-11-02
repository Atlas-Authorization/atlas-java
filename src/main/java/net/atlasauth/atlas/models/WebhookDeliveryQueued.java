package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record WebhookDeliveryQueued(
        String object,
        String id,
        String eventId,
        String status,
        String note,
        /** On a test: the URL the worker will POST to. */ String url) {

    /** The shape before {@code url}. */
    public WebhookDeliveryQueued(String object, String id, String eventId, String status, String note) {
        this(object, id, eventId, status, note, null);
    }
}
