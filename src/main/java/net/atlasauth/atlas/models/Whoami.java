package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

/**
 * {@code GET /v1/whoami} (and {@code /v1/platform/whoami}): what the credential in use is and what it may do.
 * {@code account}, {@code platform}, {@code partner} and {@code operator} are raw JSON (present for the
 * credential kinds they describe).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record Whoami(
        String object,
        Key key,
        Instance instance,
        JsonNode account,
        JsonNode platform,
        JsonNode partner,
        JsonNode operator) {

    /** The credential a request was signed with. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Key(
            String id,
            /** secret, publishable, platform, partner_link or access_token. */ String type,
            String prefix,
            String environment,
            List<String> scopes,
            List<String> effectiveScopes,
            String owner,
            Long createdAt,
            Long expiresAt,
            Long lastUsedAt,
            String lastUsedIp,
            List<String> applicationIds) {
    }

    /** The instance the key belongs to. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Instance(
            String id,
            String environment,
            String issuer,
            String discoveryUrl,
            String jwksUri) {
    }
}
