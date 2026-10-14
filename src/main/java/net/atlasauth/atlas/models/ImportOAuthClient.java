package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class ImportOAuthClient {
    /** Preserve the existing client_id for a migration. */
    public String clientId;
    /** Plaintext secret; hashed on import. Write-only. */
    public String clientSecret;
    /** A pre-computed secret hash to import verbatim (in place of client_secret). */
    public String clientSecretHash;
    public String name;
    public List<String> redirectUris;
    public List<String> allowedScopes;
    public List<String> grantTypes;
    public String tokenEndpointAuthMethod;

    @JsonIgnore
    private final Map<String, Object> additionalFields = new LinkedHashMap<>();

    public ImportOAuthClient() {
    }

    public ImportOAuthClient clientId(String v) { this.clientId = v; return this; }
    public ImportOAuthClient clientSecret(String v) { this.clientSecret = v; return this; }
    public ImportOAuthClient clientSecretHash(String v) { this.clientSecretHash = v; return this; }
    public ImportOAuthClient name(String v) { this.name = v; return this; }
    public ImportOAuthClient redirectUris(List<String> v) { this.redirectUris = v; return this; }
    public ImportOAuthClient allowedScopes(List<String> v) { this.allowedScopes = v; return this; }
    public ImportOAuthClient grantTypes(List<String> v) { this.grantTypes = v; return this; }
    public ImportOAuthClient tokenEndpointAuthMethod(String v) { this.tokenEndpointAuthMethod = v; return this; }

    /**
     * Set any field by its wire name, including an explicit {@code null} (which
     * clears a stored value on a partial update) or a field this SDK does not model yet.
     */
    public ImportOAuthClient set(String key, Object value) { this.additionalFields.put(key, value); return this; }

    @JsonAnyGetter
    public Map<String, Object> additionalFields() { return additionalFields; }
}
