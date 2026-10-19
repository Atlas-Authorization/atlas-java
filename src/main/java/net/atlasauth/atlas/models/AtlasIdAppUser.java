package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/** A person who has approved a Sign in with Atlas app. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AtlasIdAppUser(
        String object,
        boolean atlasIdUser,
        /** The user id as this app sees it (the id_token sub). */ String sub,
        String grantId,
        List<String> scopes,
        long grantedAt) {
}
