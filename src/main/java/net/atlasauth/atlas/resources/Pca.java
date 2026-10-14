package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.PcaAttestors;
import net.atlasauth.atlas.models.PcaFreezeState;
import net.atlasauth.atlas.models.PcaRevocation;
import net.atlasauth.atlas.models.PcaRevocationBody;
import net.atlasauth.atlas.models.PcaStepUp;

import java.util.List;
import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** Proof-carrying authority controls ({@code /v1/pca}). */
public final class Pca {

    private final Transport t;

    public Pca(Transport t) {
        this.t = t;
    }

    /** {@code POST /v1/pca/revocations} — Revoke a capability id under a grant; any PCActn whose chain contains it is denied. */
    public PcaRevocation revoke(PcaRevocationBody body) {
        return t.post("/v1/pca/revocations", body, new TypeReference<>() {
        });
    }

    /** As above, with an {@code Idempotency-Key} so a retry replays the first response. */
    public PcaRevocation revoke(PcaRevocationBody body, String idempotencyKey) {
        return t.post("/v1/pca/revocations", body, idempotencyKey, new TypeReference<>() {
        });
    }

    public PcaFreezeState freeze() {
        return freeze(null);
    }

    /** {@code POST /v1/pca/freeze} — Engage the instance-wide PCA kill switch: every PCActn is denied until it is cleared. */
    public PcaFreezeState freeze(String reason) {
        Map<String, Object> payload = Params.of().set("reason", reason).map();
        return t.post("/v1/pca/freeze", payload, new TypeReference<>() {
        });
    }

    /** {@code DELETE /v1/pca/freeze} — Clear the instance-wide PCA kill switch. */
    public PcaFreezeState unfreeze() {
        return t.delete("/v1/pca/freeze", new TypeReference<>() {
        });
    }

    public PcaStepUp denyStepUp(String id) {
        return denyStepUp(id, null);
    }

    /** {@code POST /v1/pca/stepups/{id}/deny} — Deny a pending step-up: the held action is never anchored. */
    public PcaStepUp denyStepUp(String id, String reason) {
        Map<String, Object> payload = Params.of().set("reason", reason).map();
        return t.post("/v1/pca/stepups/" + enc(id) + "/deny", payload, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/pca/attestors} — The instance's trusted software-attestor public keys. */
    public PcaAttestors getAttestors() {
        return t.get("/v1/pca/attestors", new TypeReference<>() {
        });
    }

    /** {@code PUT /v1/pca/attestors} — Replace the trusted software-attestor key list. */
    public PcaAttestors replaceAttestors(List<String> trustedAttestorKeys) {
        Map<String, Object> payload = Params.of().set("trusted_attestor_keys", trustedAttestorKeys).map();
        return t.put("/v1/pca/attestors", payload, new TypeReference<>() {
        });
    }
}
