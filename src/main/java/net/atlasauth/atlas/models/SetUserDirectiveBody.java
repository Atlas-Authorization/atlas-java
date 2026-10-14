package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.LinkedHashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class SetUserDirectiveBody {
    public String suspendedUntil;
    public Boolean readOnly;
    public Boolean forcedLogout;
    public String reason;

    @JsonIgnore
    private final Map<String, Object> additionalFields = new LinkedHashMap<>();

    public SetUserDirectiveBody() {
    }

    public SetUserDirectiveBody suspendedUntil(String v) { this.suspendedUntil = v; return this; }
    public SetUserDirectiveBody readOnly(Boolean v) { this.readOnly = v; return this; }
    public SetUserDirectiveBody forcedLogout(Boolean v) { this.forcedLogout = v; return this; }
    public SetUserDirectiveBody reason(String v) { this.reason = v; return this; }

    /**
     * Set any field by its wire name, including an explicit {@code null} (which
     * clears a stored value on a partial update) or a field this SDK does not model yet.
     */
    public SetUserDirectiveBody set(String key, Object value) { this.additionalFields.put(key, value); return this; }

    @JsonAnyGetter
    public Map<String, Object> additionalFields() { return additionalFields; }
}
