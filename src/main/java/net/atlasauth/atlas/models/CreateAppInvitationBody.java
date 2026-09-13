package net.atlasauth.atlas.models;

import java.util.Map;

/**
 * Body for {@code POST /v1/invitations}. Only {@code emailAddress} is required.
 * Fluent setters return {@code this}.
 */
public final class CreateAppInvitationBody {
    public String emailAddress;
    public Map<String, Object> publicMetadata;

    public CreateAppInvitationBody() {
    }

    public CreateAppInvitationBody(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public CreateAppInvitationBody emailAddress(String v) { this.emailAddress = v; return this; }
    public CreateAppInvitationBody publicMetadata(Map<String, Object> v) { this.publicMetadata = v; return this; }
}
