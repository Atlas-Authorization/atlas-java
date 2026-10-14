package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.LinkedHashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class ImportIdentityBody {
    public String provider;
    public String providerUserId;
    public String emailAddress;
    public Boolean verified;

    @JsonIgnore
    private final Map<String, Object> additionalFields = new LinkedHashMap<>();

    public ImportIdentityBody() {
    }

    public ImportIdentityBody provider(String v) { this.provider = v; return this; }
    public ImportIdentityBody providerUserId(String v) { this.providerUserId = v; return this; }
    public ImportIdentityBody emailAddress(String v) { this.emailAddress = v; return this; }
    public ImportIdentityBody verified(Boolean v) { this.verified = v; return this; }

    /**
     * Set any field by its wire name, including an explicit {@code null} (which
     * clears a stored value on a partial update) or a field this SDK does not model yet.
     */
    public ImportIdentityBody set(String key, Object value) { this.additionalFields.put(key, value); return this; }

    @JsonAnyGetter
    public Map<String, Object> additionalFields() { return additionalFields; }
}
