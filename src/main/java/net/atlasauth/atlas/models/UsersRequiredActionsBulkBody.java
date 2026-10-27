package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code POST /v1/required_actions/bulk}. Required: action. */
public final class UsersRequiredActionsBulkBody {
    /** Required. */
    public String action;
    /** custom only: the https URL the user is sent to. */
    public String url;
    public String reason;
    /** Exactly one of organization_id or user_ids. */
    public String organizationId;
    public List<String> userIds;

    public UsersRequiredActionsBulkBody() {
    }

    public UsersRequiredActionsBulkBody action(String v) { this.action = v; return this; }
    public UsersRequiredActionsBulkBody url(String v) { this.url = v; return this; }
    public UsersRequiredActionsBulkBody reason(String v) { this.reason = v; return this; }
    public UsersRequiredActionsBulkBody organizationId(String v) { this.organizationId = v; return this; }
    public UsersRequiredActionsBulkBody userIds(List<String> v) { this.userIds = v; return this; }
}
