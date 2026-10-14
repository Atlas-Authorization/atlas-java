package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.LinkedHashMap;
import java.util.Map;

/** Directory attribute → Atlas field mapping. `email` is required. */
@JsonIgnoreProperties(ignoreUnknown = true)
public final class OrgLdapConnectionBodyAttributes {
    public String email;
    @JsonProperty("firstName") public String firstName;
    @JsonProperty("lastName") public String lastName;
    public String username;

    @JsonIgnore
    private final Map<String, Object> additionalFields = new LinkedHashMap<>();

    public OrgLdapConnectionBodyAttributes() {
    }

    public OrgLdapConnectionBodyAttributes email(String v) { this.email = v; return this; }
    public OrgLdapConnectionBodyAttributes firstName(String v) { this.firstName = v; return this; }
    public OrgLdapConnectionBodyAttributes lastName(String v) { this.lastName = v; return this; }
    public OrgLdapConnectionBodyAttributes username(String v) { this.username = v; return this; }

    /**
     * Set any field by its wire name, including an explicit {@code null} (which
     * clears a stored value on a partial update) or a field this SDK does not model yet.
     */
    public OrgLdapConnectionBodyAttributes set(String key, Object value) { this.additionalFields.put(key, value); return this; }

    @JsonAnyGetter
    public Map<String, Object> additionalFields() { return additionalFields; }
}
