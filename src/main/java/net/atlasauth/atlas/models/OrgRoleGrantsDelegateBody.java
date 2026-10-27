package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code POST /v1/org_role_grants/{id}/delegations}. */
public final class OrgRoleGrantsDelegateBody {
    public String userId;
    public String roleKey;

    public OrgRoleGrantsDelegateBody() {
    }

    public OrgRoleGrantsDelegateBody userId(String v) { this.userId = v; return this; }
    public OrgRoleGrantsDelegateBody roleKey(String v) { this.roleKey = v; return this; }
}
