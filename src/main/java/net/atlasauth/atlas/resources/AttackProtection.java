package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.UpdateAttackProtectionBody;

/** The {@code /v1/attack_protection} resource. */
public final class AttackProtection {

    private final Transport t;

    public AttackProtection(Transport t) {
        this.t = t;
    }

    public net.atlasauth.atlas.models.AttackProtection get() {
        return t.get("/v1/attack_protection", new TypeReference<>() {
        });
    }

    public net.atlasauth.atlas.models.AttackProtection update(UpdateAttackProtectionBody body) {
        return t.patch("/v1/attack_protection", body, new TypeReference<>() {
        });
    }
}
