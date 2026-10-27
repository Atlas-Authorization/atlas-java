package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code PUT /v1/risk_based_mfa/reputation_secret}. */
public final class RiskBasedMfaSetReputationSecretBody {
    public String secret;
    public String provider;

    public RiskBasedMfaSetReputationSecretBody() {
    }

    public RiskBasedMfaSetReputationSecretBody secret(String v) { this.secret = v; return this; }
    public RiskBasedMfaSetReputationSecretBody provider(String v) { this.provider = v; return this; }
}
