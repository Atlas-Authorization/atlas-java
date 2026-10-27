package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code POST /v1/users/{id}/required_actions}. Required: action. */
public final class UsersRequiredActionsRequestBody {
    /** Required. */
    public String action;
    /** custom only: the https URL the user is sent to. */
    public String url;
    /** Free note shown on the audit entry and the held attempt (max 500 characters). */
    public String reason;

    public UsersRequiredActionsRequestBody() {
    }

    public UsersRequiredActionsRequestBody action(String v) { this.action = v; return this; }
    public UsersRequiredActionsRequestBody url(String v) { this.url = v; return this; }
    public UsersRequiredActionsRequestBody reason(String v) { this.reason = v; return this; }
}
