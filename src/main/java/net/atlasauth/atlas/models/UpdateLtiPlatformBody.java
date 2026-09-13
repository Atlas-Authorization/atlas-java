package net.atlasauth.atlas.models;

import java.util.List;

/** Body for {@code PATCH /v1/lti_platforms/:id}. */
public final class UpdateLtiPlatformBody {
    public String authLoginUrl;
    public String jwksUri;
    public List<String> deploymentIds;
    /** Pass {@code null} to unbind the platform from an organization. */
    public String organizationId;

    public UpdateLtiPlatformBody() {
    }

    public UpdateLtiPlatformBody authLoginUrl(String v) { this.authLoginUrl = v; return this; }
    public UpdateLtiPlatformBody jwksUri(String v) { this.jwksUri = v; return this; }
    public UpdateLtiPlatformBody deploymentIds(List<String> v) { this.deploymentIds = v; return this; }
    public UpdateLtiPlatformBody organizationId(String v) { this.organizationId = v; return this; }
}
