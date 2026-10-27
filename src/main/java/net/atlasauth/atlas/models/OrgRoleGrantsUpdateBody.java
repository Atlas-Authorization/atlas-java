package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code PATCH /v1/org_role_grants/{id}}. */
public final class OrgRoleGrantsUpdateBody {
    public List<String> roleKeys;

    public OrgRoleGrantsUpdateBody() {
    }

    public OrgRoleGrantsUpdateBody roleKeys(List<String> v) { this.roleKeys = v; return this; }
}
