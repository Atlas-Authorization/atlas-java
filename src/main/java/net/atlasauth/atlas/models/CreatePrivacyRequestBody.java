package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code POST /v1/privacy/requests}. Type is access, deletion, closure, restriction or rectification (export, erasure and erase are accepted aliases). The wire names are camelCase. */
public final class CreatePrivacyRequestBody extends ExtensibleBody {
    @JsonProperty("subjectId")
    public String subjectId;
    public String type;
    @JsonProperty("caseId")
    public String caseId;
    /** ISO 8601: nothing is erased before it. Use {@link #executeAfterMs(long)} for epoch milliseconds. */
    @JsonProperty("executeAfter")
    public String executeAfter;
    public String reason;

    public CreatePrivacyRequestBody() {
    }

    public CreatePrivacyRequestBody subjectId(String v) { this.subjectId = v; return this; }
    public CreatePrivacyRequestBody type(String v) { this.type = v; return this; }
    public CreatePrivacyRequestBody caseId(String v) { this.caseId = v; return this; }
    public CreatePrivacyRequestBody executeAfter(String v) { this.executeAfter = v; return this; }
    public CreatePrivacyRequestBody reason(String v) { this.reason = v; return this; }

    /** Set any field by its wire name, including one this SDK does not model yet. */
    public CreatePrivacyRequestBody set(String key, Object value) { putField(key, value); return this; }

    /** Send {@code executeAfter} as epoch milliseconds instead of an ISO 8601 string. */
    public CreatePrivacyRequestBody executeAfterMs(long ms) { this.executeAfter = null; putField("executeAfter", ms); return this; }
}
