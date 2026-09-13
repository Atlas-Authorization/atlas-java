package net.atlasauth.atlas.models;

/** One external (social) account attached to an imported user row. */
public final class ImportExportExternalAccount {
    public String provider;
    public String providerUserId;
    public String email;
    public Boolean emailVerified;

    public ImportExportExternalAccount() {
    }

    public ImportExportExternalAccount provider(String v) { this.provider = v; return this; }
    public ImportExportExternalAccount providerUserId(String v) { this.providerUserId = v; return this; }
    public ImportExportExternalAccount email(String v) { this.email = v; return this; }
    public ImportExportExternalAccount emailVerified(Boolean v) { this.emailVerified = v; return this; }
}
