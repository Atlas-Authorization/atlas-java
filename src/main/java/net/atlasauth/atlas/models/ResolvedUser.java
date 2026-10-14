package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ResolvedUser(
        String object,
        String userId,
        String email,
        boolean emailVerified,
        String name,
        String imageUrl,
        List<ResolvedIdentity> identities) {
}
