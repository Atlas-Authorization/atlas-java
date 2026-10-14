package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record UserErasureAck(
        String object,
        String id,
        String userId,
        String source,
        long createdAt) {
}
