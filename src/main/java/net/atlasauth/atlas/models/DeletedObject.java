package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * The minimal deletion acknowledgement several mutation routes return, e.g.
 * {@code { "object": "user", "id": "usr_123", "deleted": true }}. Extra fields
 * some routes add alongside it (counts, ids) are ignored here.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record DeletedObject(
        String object,
        String id,
        boolean deleted) {
}
