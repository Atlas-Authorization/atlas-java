package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.OAuthClientsWorkloadIdentityCloudTrustResponse;
import net.atlasauth.atlas.models.OAuthClientsWorkloadIdentityPresetsResponse;

import java.util.List;
import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** The {@code /v1/workload_identity} resource. */
public final class OAuthClientsWorkloadIdentity {

    private final Transport t;

    public OAuthClientsWorkloadIdentity(Transport t) {
        this.t = t;
    }

    /** {@code GET /v1/workload_identity/presets} - List the workload platforms Atlas knows defaults for (SPIFFE, Kubernetes, GitHub Actions, GCP, Azure): issuer, JWKS URL and the shape of the token subject. Scope: {@code oauth_clients:read}. */
    public OAuthClientsWorkloadIdentityPresetsResponse presets() {
        return presets(null, null);
    }

    /** As above, with every optional parameter. */
    public OAuthClientsWorkloadIdentityPresetsResponse presets(Long limit, String startingAfter) {
        Map<String, Object> q = Params.of().set("limit", limit).set("starting_after", startingAfter).map();
        return t.request("GET", "/v1/workload_identity/presets", q, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/workload_identity/cloud_trust} - Trust-policy snippets that make AWS (AssumeRoleWithWebIdentity), GCP (workload identity federation) or Azure (federated credentials) accept an access token issued by this instance. Nothing is changed in the cloud. Scope: {@code oauth_clients:read}. */
    public OAuthClientsWorkloadIdentityCloudTrustResponse cloudTrust(String provider, String audience, String subject) {
        return cloudTrust(provider, audience, subject, null);
    }

    /** As above, with every optional parameter. */
    public OAuthClientsWorkloadIdentityCloudTrustResponse cloudTrust(String provider, String audience, String subject, String tenantId) {
        Map<String, Object> q = Params.of().set("provider", provider).set("audience", audience).set("subject", subject).set("tenant_id", tenantId).map();
        return t.request("GET", "/v1/workload_identity/cloud_trust", q, null, null, false, new TypeReference<>() {
        });
    }
}
