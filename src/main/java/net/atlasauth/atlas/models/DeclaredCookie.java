package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** A cookie or browser-storage key Atlas writes for the instance (all strictly necessary). */
@JsonIgnoreProperties(ignoreUnknown = true)
public record DeclaredCookie(
        /** The exact name, or null when {@code pattern} describes a family of keys. */ String name,
        String pattern,
        /** cookie, localStorage, sessionStorage or indexedDB. */ String store,
        String domain,
        /** null = browser session (cookie) or until cleared (storage). */ Long lifetimeSeconds,
        String lifetime,
        boolean httpOnly,
        String category,
        String purpose,
        String writtenWhen,
        /** atlas_frontend_api or atlas_sdk. */ String setBy) {
}
