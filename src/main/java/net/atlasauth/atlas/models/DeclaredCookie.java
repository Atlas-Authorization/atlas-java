package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

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
        /** atlas_frontend_api or atlas_sdk. */ String setBy,
        /** The purpose in every language the hosted pages speak, keyed by locale. */ Map<String, String> purposes,
        /** Present when the lifetime follows an instance setting; {@code lifetimeSeconds} is this instance's value. */ DeclaredCookieLifetimeRange lifetimeRange) {

    /** The 1.18 shape, without {@code purposes} and {@code lifetimeRange}. */
    public DeclaredCookie(String name, String pattern, String store, String domain, Long lifetimeSeconds, String lifetime, boolean httpOnly, String category, String purpose, String writtenWhen, String setBy) {
        this(name, pattern, store, domain, lifetimeSeconds, lifetime, httpOnly, category, purpose, writtenWhen, setBy, null, null);
    }
}
