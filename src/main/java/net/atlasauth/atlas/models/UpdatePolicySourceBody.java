package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Body for {@code PUT /v1/instance/policy_source}: partial. {@code signingSecret} is write-only (a whsec_ secret works).
 */
public final class UpdatePolicySourceBody {
    public Boolean enabled;
    /** Base URL; Atlas calls GET {endpoint_url}/policies. */ public String endpointUrl;
    /** Write-only. */ public String signingSecret;
    /** webhook or poll. */ public String sync;
    /** Poll mode: 5-1440. */ public Integer pollIntervalMinutes;

    @JsonIgnore
    private final Map<String, Object> additionalFields = new LinkedHashMap<>();

    public UpdatePolicySourceBody() {
    }

    public UpdatePolicySourceBody enabled(Boolean v) { this.enabled = v; return this; }
    public UpdatePolicySourceBody endpointUrl(String v) { this.endpointUrl = v; return this; }
    public UpdatePolicySourceBody signingSecret(String v) { this.signingSecret = v; return this; }
    public UpdatePolicySourceBody sync(String v) { this.sync = v; return this; }
    public UpdatePolicySourceBody pollIntervalMinutes(Integer v) { this.pollIntervalMinutes = v; return this; }

    /** Clear a nullable field by its wire name ({@code endpoint_url}, {@code signing_secret}): it is sent as {@code null}. */
    public UpdatePolicySourceBody clear(String key) { this.additionalFields.put(key, null); return this; }

    /** Set any field by its wire name, including one this SDK does not model yet. */
    public UpdatePolicySourceBody set(String key, Object value) { this.additionalFields.put(key, value); return this; }

    @JsonAnyGetter
    public Map<String, Object> additionalFields() { return additionalFields; }
}
