package net.atlasauth.atlas.models;

import java.util.Map;

/**
 * Body for {@code POST /v1/users}. Only {@code emailAddress} is required; unset
 * fields are omitted from the request. Fluent setters return {@code this}.
 */
public final class CreateUserBody {
    public String emailAddress;
    public String password;
    /** An existing hash instead of {@code password}; a self-describing one (Argon2id, bcrypt, ...) is stored as given. */
    public String passwordHash;
    /** Names a hash that does not describe itself: scrypt, pbkdf2, md5, sha1, sha256, sha512, hmac_sha256, ... */
    public String passwordHashAlgorithm;
    /** The algorithm's parameters, e.g. scrypt {@code {n, r, p, key_length, salt_encoding}}. */
    public Map<String, Object> passwordHashParams;
    /** The salt, when it is not part of {@code passwordHash}. */
    public String passwordSalt;
    /** The Clerk hasher name, used only when the hash does not describe itself. */
    public String passwordHasher;
    public String firstName;
    public String lastName;
    public Boolean emailVerified;
    public Map<String, Object> publicMetadata;
    public Map<String, Object> privateMetadata;
    public Map<String, Object> unsafeMetadata;
    /**
     * {@code false} (the default) guarantees no email, SMS or push is sent to the user
     * as a consequence of the create.
     */
    public Boolean notify;
    /** Import a referral (how the user arrived); with referral attribution on, the user is reported to the referral platform. */
    public CreateUserReferral referral;
    /** The user's own referral code, unique per instance (409 {@code REFERRAL_CODE_TAKEN}). */
    public String referralCode;
    /** Absolute https avatar URL (at most 2048 characters). */
    public String imageUrl;
    /** Alias of {@code imageUrl}; send one or the other. */
    public String profileImageUrl;

    public CreateUserBody() {
    }

    public CreateUserBody(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public CreateUserBody emailAddress(String v) { this.emailAddress = v; return this; }
    public CreateUserBody password(String v) { this.password = v; return this; }
    public CreateUserBody passwordHash(String v) { this.passwordHash = v; return this; }
    public CreateUserBody passwordHashAlgorithm(String v) { this.passwordHashAlgorithm = v; return this; }
    public CreateUserBody passwordHashParams(Map<String, Object> v) { this.passwordHashParams = v; return this; }
    public CreateUserBody passwordSalt(String v) { this.passwordSalt = v; return this; }
    public CreateUserBody passwordHasher(String v) { this.passwordHasher = v; return this; }
    public CreateUserBody firstName(String v) { this.firstName = v; return this; }
    public CreateUserBody lastName(String v) { this.lastName = v; return this; }
    public CreateUserBody emailVerified(Boolean v) { this.emailVerified = v; return this; }
    public CreateUserBody publicMetadata(Map<String, Object> v) { this.publicMetadata = v; return this; }
    public CreateUserBody privateMetadata(Map<String, Object> v) { this.privateMetadata = v; return this; }
    public CreateUserBody unsafeMetadata(Map<String, Object> v) { this.unsafeMetadata = v; return this; }
    public CreateUserBody notify(Boolean v) { this.notify = v; return this; }
    public CreateUserBody referral(CreateUserReferral v) { this.referral = v; return this; }
    public CreateUserBody referralCode(String v) { this.referralCode = v; return this; }
    public CreateUserBody imageUrl(String v) { this.imageUrl = v; return this; }
    public CreateUserBody profileImageUrl(String v) { this.profileImageUrl = v; return this; }
}
