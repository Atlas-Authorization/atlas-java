package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.UpdateAttackProtectionBody;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.models.AttackProtectionRemoveAppAttestationSecretResponse;
import net.atlasauth.atlas.models.AttackProtectionSetAppAttestationSecretBody;
import net.atlasauth.atlas.models.AttackProtectionSetAppAttestationSecretResponse;
import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

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

    /** {@code PUT /v1/attack_protection/app_attestation_secret} - Store the Play Integrity response-decryption key (base64 AES-256) for app attestation. Write-only; the public verification key is attackProtection.appAttestation.android.verificationKey. Scope: {@code attack_protection:write}. */
    public AttackProtectionSetAppAttestationSecretResponse setAppAttestationSecret(AttackProtectionSetAppAttestationSecretBody body) {
        return t.request("PUT", "/v1/attack_protection/app_attestation_secret", null, body, null, false, new TypeReference<>() {
        });
    }

    /** {@code DELETE /v1/attack_protection/app_attestation_secret} - Remove a stored attestation secret: the Play Integrity decryption key (default) or, with kind=devicecheck, the Apple DeviceCheck key. That platform then refuses every request. Scope: {@code attack_protection:write}. */
    public AttackProtectionRemoveAppAttestationSecretResponse removeAppAttestationSecret() {
        return removeAppAttestationSecret(null);
    }

    /** As above, with every optional parameter. */
    public AttackProtectionRemoveAppAttestationSecretResponse removeAppAttestationSecret(String kind) {
        Map<String, Object> q = Params.of().set("kind", kind).map();
        return t.request("DELETE", "/v1/attack_protection/app_attestation_secret", q, null, null, false, new TypeReference<>() {
        });
    }
}
