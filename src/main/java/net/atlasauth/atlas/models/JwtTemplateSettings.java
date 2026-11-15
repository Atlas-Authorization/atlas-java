package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Per-template mint settings for {@code POST/PATCH /v1/jwt_templates}. Only the settings you call are sent;
 * passing {@code null} to {@link #lifetimeSeconds(Long)} or {@link #audience(String)} sends an explicit
 * {@code null}, which clears the stored value on update.
 *
 * <pre>{@code
 * new CreateJwtTemplateBody("hasura", claims)
 *     .settings(new JwtTemplateSettings().lifetimeSeconds(300L).audience("hasura"));
 * }</pre>
 */
public final class JwtTemplateSettings {

    private final Map<String, Object> fields = new LinkedHashMap<>();

    /** Token lifetime in seconds, 10 to 86400 (default: the instance session-token lifetime). {@code null} clears it. */
    public JwtTemplateSettings lifetimeSeconds(Long seconds) {
        fields.put("lifetime_seconds", seconds);
        return this;
    }

    /** A fixed {@code aud} to pin on the token. {@code null} clears it. */
    public JwtTemplateSettings audience(String audience) {
        fields.put("audience", audience);
        return this;
    }

    /** {@code RS256} (the instance JWKS, default) or {@code HS256}. */
    public JwtTemplateSettings algorithm(String algorithm) {
        fields.put("algorithm", algorithm);
        return this;
    }

    /** The HS256 shared secret, at least 16 characters. Write-only: it is never returned. */
    public JwtTemplateSettings signingSecret(String secret) {
        fields.put("signing_secret", secret);
        return this;
    }

    @JsonValue
    public Map<String, Object> toMap() {
        return fields;
    }
}
