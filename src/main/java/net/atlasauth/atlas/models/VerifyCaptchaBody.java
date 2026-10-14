package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.LinkedHashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class VerifyCaptchaBody {
    public String token;
    public String action;
    public String remoteIp;

    @JsonIgnore
    private final Map<String, Object> additionalFields = new LinkedHashMap<>();

    public VerifyCaptchaBody() {
    }

    public VerifyCaptchaBody token(String v) { this.token = v; return this; }
    public VerifyCaptchaBody action(String v) { this.action = v; return this; }
    public VerifyCaptchaBody remoteIp(String v) { this.remoteIp = v; return this; }

    /**
     * Set any field by its wire name, including an explicit {@code null} (which
     * clears a stored value on a partial update) or a field this SDK does not model yet.
     */
    public VerifyCaptchaBody set(String key, Object value) { this.additionalFields.put(key, value); return this; }

    @JsonAnyGetter
    public Map<String, Object> additionalFields() { return additionalFields; }
}
