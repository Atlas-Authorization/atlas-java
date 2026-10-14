package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ApiKeyToken(
        String object,
        String token,
        String tokenType,
        String jti,
        String keyId,
        String subjectType,
        String subjectId,
        List<String> scopes,
        Map<String, Object> constraints,
        ApiKeyTokenCnf cnf,
        long expiresIn,
        long expiresAt,
        String issuer) {
}
