package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.UpdateRiskBasedMfaBody;

/** The {@code /v1/risk_based_mfa} resource. */
public final class RiskBasedMfa {

    private final Transport t;

    public RiskBasedMfa(Transport t) {
        this.t = t;
    }

    /** Read the current adaptive-MFA config. */
    public net.atlasauth.atlas.models.RiskBasedMfa get() {
        return t.get("/v1/risk_based_mfa", new TypeReference<>() {
        });
    }

    /** Change any subset of the config; omitted fields are left unchanged. */
    public net.atlasauth.atlas.models.RiskBasedMfa update(UpdateRiskBasedMfaBody body) {
        return t.patch("/v1/risk_based_mfa", body, new TypeReference<>() {
        });
    }
}
