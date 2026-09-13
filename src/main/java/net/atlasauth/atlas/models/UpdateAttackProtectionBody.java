package net.atlasauth.atlas.models;

/**
 * Body for {@code PATCH /v1/attack_protection}. Only the two flags are writable;
 * the rest of the object is read-only.
 */
public final class UpdateAttackProtectionBody {
    public AttackProtectionFlagBody bruteForce;
    public AttackProtectionFlagBody breachedPassword;

    public UpdateAttackProtectionBody bruteForce(AttackProtectionFlagBody v) { this.bruteForce = v; return this; }
    public UpdateAttackProtectionBody breachedPassword(AttackProtectionFlagBody v) { this.breachedPassword = v; return this; }
}
