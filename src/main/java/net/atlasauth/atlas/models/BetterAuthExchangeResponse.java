package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * {@code POST /v1/migrations/better_auth/exchange}: {@code object} {@code session_tokens} ({@code jwt},
 * {@code refreshToken}, {@code sessionId}, {@code userId}, {@code expiresIn}), or {@code sign_in_attempt}
 * ({@code id}, {@code status} {@code needs_second_factor} / {@code needs_mfa_enrollment}) when a second
 * factor is still owed.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record BetterAuthExchangeResponse(
        String object,
        String id,
        String sessionId,
        String userId,
        String jwt,
        String refreshToken,
        Long expiresIn,
        String status) {
}
