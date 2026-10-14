package net.atlasauth.atlas.verify;

import java.util.Map;

/**
 * The verified claims of a back-channel {@code logout_token}. End every local
 * session matching {@code sid} (when present), or every session of {@code sub}.
 *
 * @param sub    the user whose session ended (may be null when {@code sid} is set)
 * @param sid    the Atlas session id your ID tokens carried (may be null when {@code sub} is set)
 * @param jti    the token's unique id — record it to reject a replay
 * @param iat    issued-at, in seconds since the epoch
 * @param claims every claim, as decoded
 */
public record LogoutTokenClaims(String sub, String sid, String jti, long iat, Map<String, Object> claims) {
}
