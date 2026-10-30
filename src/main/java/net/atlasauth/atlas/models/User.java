package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Map;

/** A user as the BAPI serves it. {@code private_metadata} is never returned. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record User(
        String object,
        String id,
        String username,
        String firstName,
        String lastName,
        String imageUrl,
        Map<String, Object> publicMetadata,
        boolean mfaEnabled,
        boolean banned,
        boolean locked,
        Long lastSignInAt,
        long createdAt,
        long updatedAt,
        /** Strictly increases with every change to the user; webhook payloads carry the same value. */ Long version,
        /** Where the user's referral stands, or null when the user was never reported to the referral platform. */ UserReferral referral,
        /** The user's own referral code, when they are a referrer. */ String referralCode,
        /** The user's own referral link, when they are a referrer. */ String referralLink,
        /** Your own id for the user, if set ({@code users().setExternalId}). */ String externalId,
        /** When a timed ban lifts (epoch ms); null for a permanent ban or no ban. */ Long banExpiresAt,
        /** The provider identities linked to the user (from any source, incl. imports). Tokens are never included. */ List<UserExternalAccount> externalAccounts) {

    /** The 1.18 shape, without {@code externalAccounts}. */
    public User(String object, String id, String username, String firstName, String lastName, String imageUrl, Map<String, Object> publicMetadata, boolean mfaEnabled, boolean banned, boolean locked, Long lastSignInAt, long createdAt, long updatedAt, Long version, UserReferral referral, String referralCode, String referralLink, String externalId, Long banExpiresAt) {
        this(object, id, username, firstName, lastName, imageUrl, publicMetadata, mfaEnabled, banned, locked, lastSignInAt, createdAt, updatedAt, version, referral, referralCode, referralLink, externalId, banExpiresAt, null);
    }

    /** The 1.10 shape, before the 1.11 fields. */
    public User(String object, String id, String username, String firstName, String lastName, String imageUrl, Map<String, Object> publicMetadata, boolean mfaEnabled, boolean banned, boolean locked, Long lastSignInAt, long createdAt, long updatedAt, Long version, UserReferral referral, String referralCode, String referralLink, String externalId) {
        this(object, id, username, firstName, lastName, imageUrl, publicMetadata, mfaEnabled, banned, locked, lastSignInAt, createdAt, updatedAt, version, referral, referralCode, referralLink, externalId, null);
    }

    /** The previous shape, without {@code version}. */
    public User(String object, String id, String username, String firstName, String lastName, String imageUrl, Map<String, Object> publicMetadata, boolean mfaEnabled, boolean banned, boolean locked, Long lastSignInAt, long createdAt, long updatedAt) {
        this(object, id, username, firstName, lastName, imageUrl, publicMetadata, mfaEnabled, banned, locked, lastSignInAt, createdAt, updatedAt, null, null, null, null, null);
    }

    /** The 1.6 shape, without the referral fields. */
    public User(String object, String id, String username, String firstName, String lastName, String imageUrl, Map<String, Object> publicMetadata, boolean mfaEnabled, boolean banned, boolean locked, Long lastSignInAt, long createdAt, long updatedAt, Long version) {
        this(object, id, username, firstName, lastName, imageUrl, publicMetadata, mfaEnabled, banned, locked, lastSignInAt, createdAt, updatedAt, version, null, null, null, null);
    }

    /** The 1.7 shape, without {@code externalId}. */
    public User(String object, String id, String username, String firstName, String lastName, String imageUrl, Map<String, Object> publicMetadata, boolean mfaEnabled, boolean banned, boolean locked, Long lastSignInAt, long createdAt, long updatedAt, Long version, UserReferral referral, String referralCode, String referralLink) {
        this(object, id, username, firstName, lastName, imageUrl, publicMetadata, mfaEnabled, banned, locked, lastSignInAt, createdAt, updatedAt, version, referral, referralCode, referralLink, null);
    }
}
