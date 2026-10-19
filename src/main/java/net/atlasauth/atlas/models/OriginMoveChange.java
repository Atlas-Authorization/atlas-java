package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import com.fasterxml.jackson.databind.JsonNode;

/** One field an origin move rewrites: {@code before} and {@code after} are a URL, a list of URLs, or null. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record OriginMoveChange(
        /** instance, oauth_client, atlas_id_app, webhook_endpoint or redirect_url. */ String object,
        String id,
        String field,
        JsonNode before,
        JsonNode after) {
}
