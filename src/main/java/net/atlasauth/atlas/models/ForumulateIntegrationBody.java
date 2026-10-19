package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** The body of {@code PUT /v1/instance/integrations/forumulate}; replaces the whole connection. */
@JsonIgnoreProperties(ignoreUnknown = true)
public final class ForumulateIntegrationBody {
    /** Your Forumulate organization id. */
    public String orgId;
    /** 1-50 space ids, or maps with id and redeem_url. */
    public List<?> spaces;
    /** The token aud. Default forumulate:&lt;org_id&gt;. */
    public String audience;

    @JsonIgnore
    private final Map<String, Object> additionalFields = new LinkedHashMap<>();

    public ForumulateIntegrationBody() {
    }

    public ForumulateIntegrationBody orgId(String v) { this.orgId = v; return this; }
    public ForumulateIntegrationBody spaces(List<?> v) { this.spaces = v; return this; }
    public ForumulateIntegrationBody audience(String v) { this.audience = v; return this; }

    /**
     * Set any field by its wire name, including an explicit {@code null} (which
     * clears a stored value on a partial update) or a field this SDK does not model yet.
     */
    public ForumulateIntegrationBody set(String key, Object value) { this.additionalFields.put(key, value); return this; }

    @JsonAnyGetter
    public Map<String, Object> additionalFields() { return additionalFields; }
}
