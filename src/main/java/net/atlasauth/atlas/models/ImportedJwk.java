package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

/** A Better Auth JWT key Atlas publishes in the instance JWKS for the cutover. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ImportedJwk(String object, String kid, String source, Map<String, Object> jwk, String importId,
                          Long expiresAt, Long createdAt) {
}
