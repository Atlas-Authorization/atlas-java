package net.atlasauth.atlas.models;

/** The single writable {@code { enabled }} toggle nested in {@link UpdateAttackProtectionBody}. */
public final class AttackProtectionFlagBody {
    public Boolean enabled;

    public AttackProtectionFlagBody() {
    }

    public AttackProtectionFlagBody(Boolean enabled) {
        this.enabled = enabled;
    }

    public AttackProtectionFlagBody enabled(Boolean v) { this.enabled = v; return this; }
}
