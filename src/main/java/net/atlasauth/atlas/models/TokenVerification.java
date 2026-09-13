package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * The result of {@code POST /v1/tokens/verify}. On failure only {@code verified}
 * (false) and {@code reason} are set; on success {@code reason} is null and the
 * identity/session fields are populated.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record TokenVerification(
        String object,
        boolean verified,
        String reason,
        String userId,
        String sessionId,
        String organizationId,
        String organizationRole,
        List<String> organizationPermissions,
        Boolean mfa,
        boolean checkedAuthoritatively) {
}
