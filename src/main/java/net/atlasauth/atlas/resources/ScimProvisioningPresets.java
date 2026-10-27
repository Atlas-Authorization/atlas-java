package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.ScimProvisioningPresetsListResponse;

import java.util.List;
import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** The {@code /v1/scim_provisioning_presets} resource. */
public final class ScimProvisioningPresets {

    private final Transport t;

    public ScimProvisioningPresets(Transport t) {
        this.t = t;
    }

    /** {@code GET /v1/scim_provisioning_presets} - List the pre-built outbound provisioning targets (Slack, GitHub, Zoom, Salesforce, Atlassian) with their base-URL template and required parameters. Scope: {@code scim_provisioning:read}. */
    public ScimProvisioningPresetsListResponse list() {
        return list(null, null);
    }

    /** As above, with every optional parameter. */
    public ScimProvisioningPresetsListResponse list(Long limit, String startingAfter) {
        Map<String, Object> q = Params.of().set("limit", limit).set("starting_after", startingAfter).map();
        return t.request("GET", "/v1/scim_provisioning_presets", q, null, null, false, new TypeReference<>() {
        });
    }
}
