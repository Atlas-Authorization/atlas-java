package net.atlasauth.atlas.models;

import java.util.List;

/** Body for {@code POST /v1/oauth_clients}. */
public final class CreateOAuthClientBody {
    public String name;
    public List<String> redirectUris;
    public List<String> allowedScopes;
    public List<String> grantTypes;
    public String tokenEndpointAuthMethod;
    public String logoUrl;
    public Boolean firstParty;

    public CreateOAuthClientBody() {
    }

    public CreateOAuthClientBody(String name, List<String> redirectUris) {
        this.name = name;
        this.redirectUris = redirectUris;
    }

    public CreateOAuthClientBody name(String v) { this.name = v; return this; }
    public CreateOAuthClientBody redirectUris(List<String> v) { this.redirectUris = v; return this; }
    public CreateOAuthClientBody allowedScopes(List<String> v) { this.allowedScopes = v; return this; }
    public CreateOAuthClientBody grantTypes(List<String> v) { this.grantTypes = v; return this; }
    public CreateOAuthClientBody tokenEndpointAuthMethod(String v) { this.tokenEndpointAuthMethod = v; return this; }
    public CreateOAuthClientBody logoUrl(String v) { this.logoUrl = v; return this; }
    public CreateOAuthClientBody firstParty(Boolean v) { this.firstParty = v; return this; }
}
