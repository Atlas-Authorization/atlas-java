package net.atlasauth.atlas.models;

import java.util.Map;
import java.util.LinkedHashMap;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
/**
 * Body for {@code PATCH /v1/attack_protection}. Only the two flags are writable;
 * the rest of the object is read-only.
 */
public final class UpdateAttackProtectionBody {
    public AttackProtectionFlagBody bruteForce;
    public AttackProtectionFlagBody breachedPassword;
    /** The dormant-account reactivation policy (any subset). */
    public DormantAccountsSettings dormantAccounts;

    @JsonIgnore
    private final Map<String, Object> additionalFields = new LinkedHashMap<>();

    public UpdateAttackProtectionBody bruteForce(AttackProtectionFlagBody v) { this.bruteForce = v; return this; }
    public UpdateAttackProtectionBody breachedPassword(AttackProtectionFlagBody v) { this.breachedPassword = v; return this; }

    public UpdateAttackProtectionBody dormantAccounts(DormantAccountsSettings v) { this.dormantAccounts = v; return this; }

    /**
     * Set any field by its wire name, including an explicit {@code null} (which
     * clears a stored value on a partial update) or a field this SDK does not model yet.
     */
    public UpdateAttackProtectionBody set(String key, Object value) { this.additionalFields.put(key, value); return this; }

    @JsonAnyGetter
    public Map<String, Object> additionalFields() { return additionalFields; }
}
