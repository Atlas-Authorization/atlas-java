package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Body for {@code PATCH /v1/policies/:id}: only the fields set change.
 */
public final class UpdatePolicyBody {
    public String title;
    public String display;
    public Boolean required;
    public String appliesTo;
    public String reaccept;
    public Integer graceDays;
    public Integer position;
    /** active or retired. */ public String status;

    @JsonIgnore
    private final Map<String, Object> additionalFields = new LinkedHashMap<>();

    public UpdatePolicyBody() {
    }

    public UpdatePolicyBody title(String v) { this.title = v; return this; }
    public UpdatePolicyBody display(String v) { this.display = v; return this; }
    public UpdatePolicyBody required(Boolean v) { this.required = v; return this; }
    public UpdatePolicyBody appliesTo(String v) { this.appliesTo = v; return this; }
    public UpdatePolicyBody reaccept(String v) { this.reaccept = v; return this; }
    public UpdatePolicyBody graceDays(Integer v) { this.graceDays = v; return this; }
    public UpdatePolicyBody position(Integer v) { this.position = v; return this; }
    public UpdatePolicyBody status(String v) { this.status = v; return this; }

    /** Clear a nullable field by its wire name ({@code grace_days}): it is sent as {@code null}. */
    public UpdatePolicyBody clear(String key) { this.additionalFields.put(key, null); return this; }

    /** Set any field by its wire name, including one this SDK does not model yet. */
    public UpdatePolicyBody set(String key, Object value) { this.additionalFields.put(key, value); return this; }

    @JsonAnyGetter
    public Map<String, Object> additionalFields() { return additionalFields; }
}
