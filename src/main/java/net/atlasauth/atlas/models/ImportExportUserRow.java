package net.atlasauth.atlas.models;

import java.util.List;
import java.util.Map;

/** One user row accepted by an import. Provide {@code password} or a {@code passwordHash}. */
public final class ImportExportUserRow {
    public String emailAddress;
    public String email;
    public String password;
    public String passwordHash;
    /** Names a hash that does not describe itself: scrypt, pbkdf2, md5, sha1, sha256, sha512, their _salted forms, hmac_sha256, bcrypt, ... */
    public String passwordHashAlgorithm;
    /** The algorithm's parameters, e.g. scrypt {@code n} / {@code r} / {@code p} / {@code key_length}, {@code salt_encoding}. */
    public Map<String, Object> passwordHashParams;
    /** The salt, when it is not part of {@code passwordHash}. */
    public String passwordSalt;
    public Boolean emailVerified;
    public Boolean verified;
    public String firstName;
    public String lastName;
    public String username;
    public Object publicMetadata;
    public Object privateMetadata;
    public List<ImportExportExternalAccount> externalAccounts;
    /** Absolute https avatar URL; applied on create and upsert. */
    public String imageUrl;
    /** Alias of {@code imageUrl}. */
    public String profileImageUrl;

    public ImportExportUserRow() {
    }

    public ImportExportUserRow emailAddress(String v) { this.emailAddress = v; return this; }
    public ImportExportUserRow email(String v) { this.email = v; return this; }
    public ImportExportUserRow password(String v) { this.password = v; return this; }
    public ImportExportUserRow passwordHash(String v) { this.passwordHash = v; return this; }
    public ImportExportUserRow passwordHashAlgorithm(String v) { this.passwordHashAlgorithm = v; return this; }
    public ImportExportUserRow passwordHashParams(Map<String, Object> v) { this.passwordHashParams = v; return this; }
    public ImportExportUserRow passwordSalt(String v) { this.passwordSalt = v; return this; }
    public ImportExportUserRow emailVerified(Boolean v) { this.emailVerified = v; return this; }
    public ImportExportUserRow verified(Boolean v) { this.verified = v; return this; }
    public ImportExportUserRow firstName(String v) { this.firstName = v; return this; }
    public ImportExportUserRow lastName(String v) { this.lastName = v; return this; }
    public ImportExportUserRow username(String v) { this.username = v; return this; }
    public ImportExportUserRow publicMetadata(Object v) { this.publicMetadata = v; return this; }
    public ImportExportUserRow privateMetadata(Object v) { this.privateMetadata = v; return this; }
    public ImportExportUserRow externalAccounts(List<ImportExportExternalAccount> v) { this.externalAccounts = v; return this; }
    public ImportExportUserRow imageUrl(String v) { this.imageUrl = v; return this; }
    public ImportExportUserRow profileImageUrl(String v) { this.profileImageUrl = v; return this; }
}
