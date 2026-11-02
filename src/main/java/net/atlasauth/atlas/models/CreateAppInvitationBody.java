package net.atlasauth.atlas.models;

import java.util.Map;

/**
 * Body for {@code POST /v1/invitations}. Only {@code emailAddress} is required.
 * Fluent setters return {@code this}.
 */
public final class CreateAppInvitationBody {
    public String emailAddress;
    public Map<String, Object> publicMetadata;
    /** When it expires: epoch ms (Long) or an ISO 8601 date-time (String), at most 365 days ahead. Default 7 days. */
    public Object expiresAt;
    /** Alternative to {@code expiresAt}: 1 to 365 days from now. Send one or the other. */
    public Integer expiresInDays;

    public CreateAppInvitationBody() {
    }

    public CreateAppInvitationBody(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public CreateAppInvitationBody emailAddress(String v) { this.emailAddress = v; return this; }
    public CreateAppInvitationBody publicMetadata(Map<String, Object> v) { this.publicMetadata = v; return this; }
    public CreateAppInvitationBody expiresAt(long epochMs) { this.expiresAt = epochMs; return this; }
    public CreateAppInvitationBody expiresAt(String iso8601) { this.expiresAt = iso8601; return this; }
    public CreateAppInvitationBody expiresInDays(Integer v) { this.expiresInDays = v; return this; }
}
