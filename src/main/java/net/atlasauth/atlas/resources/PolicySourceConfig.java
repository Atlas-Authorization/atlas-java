package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.PolicySource;
import net.atlasauth.atlas.models.PolicySourceDeliveryPage;
import net.atlasauth.atlas.models.PolicySourceSync;
import net.atlasauth.atlas.models.PolicySourceTest;
import net.atlasauth.atlas.models.UpdatePolicySourceBody;

/**
 * The external policy source ({@code /v1/instance/policy_source}): Atlas fetches
 * {@code GET {endpoint_url}/policies} signed like an Atlas webhook, and your system tells Atlas
 * about a new version with a signed {@code policy.published} / {@code policy.retired} event sent to
 * {@code webhookUrl}. The signing secret is write-only. The helpers that verify Atlas's request and
 * sign your events are in {@link net.atlasauth.atlas.policy.PolicySourceProtocol}.
 */
public final class PolicySourceConfig {
    private final Transport t;

    public PolicySourceConfig(Transport t) {
        this.t = t;
    }

    /** {@code GET /v1/instance/policy_source}. */
    public PolicySource get() {
        return t.get("/v1/instance/policy_source", new TypeReference<>() {
        });
    }

    /** {@code PUT /v1/instance/policy_source} — partial; {@code signingSecret} is write-only. */
    public PolicySource update(UpdatePolicySourceBody body) {
        return t.put("/v1/instance/policy_source", body, new TypeReference<>() {
        });
    }

    /** {@code DELETE /v1/instance/policy_source} — disconnect the source (its policies and history stay). */
    public PolicySource delete() {
        return t.delete("/v1/instance/policy_source", new TypeReference<>() {
        });
    }

    /** {@code POST /v1/instance/policy_source/test} — a signed fetch of the feed, validated but not stored. */
    public PolicySourceTest test() {
        return t.post("/v1/instance/policy_source/test", null, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/instance/policy_source/sync} — fetch and store now. */
    public PolicySourceSync sync() {
        return t.post("/v1/instance/policy_source/sync", null, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/instance/policy_source/deliveries}. */
    public PolicySourceDeliveryPage listDeliveries() {
        return listDeliveries(null, null, null, null);
    }

    /** {@code GET /v1/instance/policy_source/deliveries}; each filter may be null. */
    public PolicySourceDeliveryPage listDeliveries(Integer limit, String startingAfter, String kind, String status) {
        return t.get("/v1/instance/policy_source/deliveries",
                Params.of().set("limit", limit).set("starting_after", startingAfter).set("kind", kind)
                        .set("status", status).map(),
                new TypeReference<>() {
                });
    }
}
