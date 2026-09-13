package net.atlasauth.atlas.models;

/**
 * Write-only credential config nested in {@link UpdateManagedWafBody}. The secret
 * fields are accepted, encrypted at rest, and never returned.
 */
public final class ManagedWafCredentialBody {
    /** {@code "keys"} or {@code "role"}. */
    public String mode;
    public String accessKeyId;
    /** Write-only: accepted, encrypted at rest, never returned. */
    public String secretAccessKey;
    public String roleArn;
    /** Write-only: accepted, encrypted at rest, never returned. */
    public String externalId;

    public ManagedWafCredentialBody mode(String v) { this.mode = v; return this; }
    public ManagedWafCredentialBody accessKeyId(String v) { this.accessKeyId = v; return this; }
    public ManagedWafCredentialBody secretAccessKey(String v) { this.secretAccessKey = v; return this; }
    public ManagedWafCredentialBody roleArn(String v) { this.roleArn = v; return this; }
    public ManagedWafCredentialBody externalId(String v) { this.externalId = v; return this; }
}
