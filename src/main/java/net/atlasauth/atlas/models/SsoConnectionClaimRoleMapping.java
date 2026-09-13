package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A single claim -&gt; role mapping on an SSO connection. Used both as a response
 * value and inside create/update bodies, so it round-trips in both directions.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record SsoConnectionClaimRoleMapping(
        String claim,
        String value,
        /** camelCase on the wire, unlike the surrounding snake_case fields. */
        @JsonProperty("roleKey") String roleKey) {
}
