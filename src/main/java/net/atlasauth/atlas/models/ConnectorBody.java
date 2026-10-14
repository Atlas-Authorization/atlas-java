package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class ConnectorBody {
    public String slug;
    public String displayName;
    public String logoUrl;
    public String tagline;
    public String description;
    public String category;
    public List<String> tags;
    public String websiteUrl;
    public String docsUrl;
    public String supportUrl;
    public List<Object> screenshots;
    public String setupGuide;
    public String protocol;
    public String authorizationEndpoint;
    public String tokenEndpoint;
    public String userinfoEndpoint;
    public String issuer;
    public List<String> defaultScopes;
    public List<Object> apiScopes;
    public List<Object> credentialFields;
    public Map<String, Object> authorizationParams;
    public Map<String, Object> quirks;
    public List<String> shareModes;
    public String idpInstanceId;
    public String idpIssuer;
    public Boolean idpAutoRegister;

    @JsonIgnore
    private final Map<String, Object> additionalFields = new LinkedHashMap<>();

    public ConnectorBody() {
    }

    public ConnectorBody slug(String v) { this.slug = v; return this; }
    public ConnectorBody displayName(String v) { this.displayName = v; return this; }
    public ConnectorBody logoUrl(String v) { this.logoUrl = v; return this; }
    public ConnectorBody tagline(String v) { this.tagline = v; return this; }
    public ConnectorBody description(String v) { this.description = v; return this; }
    public ConnectorBody category(String v) { this.category = v; return this; }
    public ConnectorBody tags(List<String> v) { this.tags = v; return this; }
    public ConnectorBody websiteUrl(String v) { this.websiteUrl = v; return this; }
    public ConnectorBody docsUrl(String v) { this.docsUrl = v; return this; }
    public ConnectorBody supportUrl(String v) { this.supportUrl = v; return this; }
    public ConnectorBody screenshots(List<Object> v) { this.screenshots = v; return this; }
    public ConnectorBody setupGuide(String v) { this.setupGuide = v; return this; }
    public ConnectorBody protocol(String v) { this.protocol = v; return this; }
    public ConnectorBody authorizationEndpoint(String v) { this.authorizationEndpoint = v; return this; }
    public ConnectorBody tokenEndpoint(String v) { this.tokenEndpoint = v; return this; }
    public ConnectorBody userinfoEndpoint(String v) { this.userinfoEndpoint = v; return this; }
    public ConnectorBody issuer(String v) { this.issuer = v; return this; }
    public ConnectorBody defaultScopes(List<String> v) { this.defaultScopes = v; return this; }
    public ConnectorBody apiScopes(List<Object> v) { this.apiScopes = v; return this; }
    public ConnectorBody credentialFields(List<Object> v) { this.credentialFields = v; return this; }
    public ConnectorBody authorizationParams(Map<String, Object> v) { this.authorizationParams = v; return this; }
    public ConnectorBody quirks(Map<String, Object> v) { this.quirks = v; return this; }
    public ConnectorBody shareModes(List<String> v) { this.shareModes = v; return this; }
    public ConnectorBody idpInstanceId(String v) { this.idpInstanceId = v; return this; }
    public ConnectorBody idpIssuer(String v) { this.idpIssuer = v; return this; }
    public ConnectorBody idpAutoRegister(Boolean v) { this.idpAutoRegister = v; return this; }

    /**
     * Set any field by its wire name, including an explicit {@code null} (which
     * clears a stored value on a partial update) or a field this SDK does not model yet.
     */
    public ConnectorBody set(String key, Object value) { this.additionalFields.put(key, value); return this; }

    @JsonAnyGetter
    public Map<String, Object> additionalFields() { return additionalFields; }
}
