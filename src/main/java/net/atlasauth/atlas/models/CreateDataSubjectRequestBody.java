package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.LinkedHashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class CreateDataSubjectRequestBody {
    /** The subject's Atlas id. */
    public String userId;
    /** The subject's email address (verified, else unverified). Resolves to the user so a rights orchestrator needn't translate email→id itself. */
    public String subjectEmail;
    public String type;
    public String reason;

    @JsonIgnore
    private final Map<String, Object> additionalFields = new LinkedHashMap<>();

    public CreateDataSubjectRequestBody() {
    }

    public CreateDataSubjectRequestBody userId(String v) { this.userId = v; return this; }
    public CreateDataSubjectRequestBody subjectEmail(String v) { this.subjectEmail = v; return this; }
    public CreateDataSubjectRequestBody type(String v) { this.type = v; return this; }
    public CreateDataSubjectRequestBody reason(String v) { this.reason = v; return this; }

    /**
     * Set any field by its wire name, including an explicit {@code null} (which
     * clears a stored value on a partial update) or a field this SDK does not model yet.
     */
    public CreateDataSubjectRequestBody set(String key, Object value) { this.additionalFields.put(key, value); return this; }

    @JsonAnyGetter
    public Map<String, Object> additionalFields() { return additionalFields; }
}
