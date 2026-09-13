package net.atlasauth.atlas.models;

import java.util.List;

/** Body for {@code POST /v1/lti_platforms}. */
public final class CreateLtiPlatformBody {
    public String issuer;
    public String clientId;
    /** An absolute https URL. */
    public String authLoginUrl;
    /** An absolute https URL. */
    public String jwksUri;
    /** At least one deployment id is required. */
    public List<String> deploymentIds;
    public String organizationId;

    public CreateLtiPlatformBody() {
    }

    public CreateLtiPlatformBody(String issuer, String clientId, String authLoginUrl, String jwksUri,
                                 List<String> deploymentIds) {
        this.issuer = issuer;
        this.clientId = clientId;
        this.authLoginUrl = authLoginUrl;
        this.jwksUri = jwksUri;
        this.deploymentIds = deploymentIds;
    }

    public CreateLtiPlatformBody issuer(String v) { this.issuer = v; return this; }
    public CreateLtiPlatformBody clientId(String v) { this.clientId = v; return this; }
    public CreateLtiPlatformBody authLoginUrl(String v) { this.authLoginUrl = v; return this; }
    public CreateLtiPlatformBody jwksUri(String v) { this.jwksUri = v; return this; }
    public CreateLtiPlatformBody deploymentIds(List<String> v) { this.deploymentIds = v; return this; }
    public CreateLtiPlatformBody organizationId(String v) { this.organizationId = v; return this; }
}
