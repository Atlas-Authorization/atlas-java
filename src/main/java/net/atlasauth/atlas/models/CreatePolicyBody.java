package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Body for {@code POST /v1/policies}.
 */
public final class CreatePolicyBody {
    /** Slug (lowercase letters, digits, _ and -), unique per instance. Becomes the consent record's policy. */ public String key;
    public String title;
    /** checkbox, scroll or link. */ public String display;
    public Boolean required;
    /** sign_up or sign_in_and_sign_up. */ public String appliesTo;
    /** none, next_sign_in or grace. */ public String reaccept;
    /** Required with reaccept grace (1-365). */ public Integer graceDays;
    public Integer position;

    @JsonIgnore
    private final Map<String, Object> additionalFields = new LinkedHashMap<>();

    public CreatePolicyBody() {
    }

    public CreatePolicyBody key(String v) { this.key = v; return this; }
    public CreatePolicyBody title(String v) { this.title = v; return this; }
    public CreatePolicyBody display(String v) { this.display = v; return this; }
    public CreatePolicyBody required(Boolean v) { this.required = v; return this; }
    public CreatePolicyBody appliesTo(String v) { this.appliesTo = v; return this; }
    public CreatePolicyBody reaccept(String v) { this.reaccept = v; return this; }
    public CreatePolicyBody graceDays(Integer v) { this.graceDays = v; return this; }
    public CreatePolicyBody position(Integer v) { this.position = v; return this; }

    /** Clear a nullable field by its wire name ({@code grace_days}): it is sent as {@code null}. */
    public CreatePolicyBody clear(String key) { this.additionalFields.put(key, null); return this; }

    /** Set any field by its wire name, including one this SDK does not model yet. */
    public CreatePolicyBody set(String key, Object value) { this.additionalFields.put(key, value); return this; }

    @JsonAnyGetter
    public Map<String, Object> additionalFields() { return additionalFields; }
}
