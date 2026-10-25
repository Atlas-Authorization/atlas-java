package net.atlasauth.atlas.models;

/** Body for {@code POST /v1/platform/atlas_id_apps/{id}/provisioned_accounts}. The password is never printed. */
public final class CreateAtlasIdProvisionedAccountBody extends ExtensibleBody {
    public String email;
    /** What the account is for, e.g. "app-store reviewer". */
    public String label;
    /** At least 12 characters. Omit it and Atlas generates one. Returned once either way. */
    public String password;
    /** Default true. */
    public Boolean emailVerified;
    /** Never asked for a second factor when signing in to this app. A risk you opt into. */
    public Boolean twoFactorExempt;
    /** Epoch ms or ISO 8601; omit or leave it for a non-expiring account. */
    public Object expiresAt;

    public CreateAtlasIdProvisionedAccountBody() {
    }

    public CreateAtlasIdProvisionedAccountBody email(String v) { this.email = v; return this; }
    public CreateAtlasIdProvisionedAccountBody label(String v) { this.label = v; return this; }
    public CreateAtlasIdProvisionedAccountBody password(String v) { this.password = v; return this; }
    public CreateAtlasIdProvisionedAccountBody emailVerified(Boolean v) { this.emailVerified = v; return this; }
    public CreateAtlasIdProvisionedAccountBody twoFactorExempt(Boolean v) { this.twoFactorExempt = v; return this; }
    /** Expiry as epoch milliseconds. */
    public CreateAtlasIdProvisionedAccountBody expiresAt(Long epochMs) { this.expiresAt = epochMs; return this; }
    /** Expiry as an ISO 8601 timestamp. */
    public CreateAtlasIdProvisionedAccountBody expiresAtIso(String iso) { this.expiresAt = iso; return this; }

    /** Set any field by its wire name, including one this SDK does not model yet. */
    public CreateAtlasIdProvisionedAccountBody set(String key, Object value) { putField(key, value); return this; }

    @Override
    public String toString() {
        return "CreateAtlasIdProvisionedAccountBody[email=" + email + ", label=" + label
                + ", password=" + (password == null ? "unset" : "***") + "]";
    }
}
