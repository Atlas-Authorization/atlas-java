package net.atlasauth.atlas.models;

import java.util.List;

/** Body for {@code PATCH /v1/instance/security}. */
public final class UpdateInstanceSecurityBody {
    public InstanceKillSwitches killSwitches;
    /** CIDRs / IPs; {@code []} clears the allowlist. Every entry is validated. */
    public List<String> ipAllowlist;
    /**
     * Required to set an allowlist that omits the caller's own IP — which would
     * lock THIS management API out irreversibly. Otherwise such a list is a 422.
     */
    public Boolean confirmLockout;

    public UpdateInstanceSecurityBody killSwitches(InstanceKillSwitches v) { this.killSwitches = v; return this; }
    public UpdateInstanceSecurityBody ipAllowlist(List<String> v) { this.ipAllowlist = v; return this; }
    public UpdateInstanceSecurityBody confirmLockout(Boolean v) { this.confirmLockout = v; return this; }
}
