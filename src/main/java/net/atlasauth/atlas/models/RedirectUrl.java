package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RedirectUrl(
        String object,
        String id,
        String url,
        long createdAt) {
}
