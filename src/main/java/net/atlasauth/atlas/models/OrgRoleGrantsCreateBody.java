package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code POST /v1/org_role_grants}. */
public final class OrgRoleGrantsCreateBody {
    public String grantorOrgId;
    public String granteeOrgId;
    public List<String> roleKeys;

    public OrgRoleGrantsCreateBody() {
    }

    public OrgRoleGrantsCreateBody grantorOrgId(String v) { this.grantorOrgId = v; return this; }
    public OrgRoleGrantsCreateBody granteeOrgId(String v) { this.granteeOrgId = v; return this; }
    public OrgRoleGrantsCreateBody roleKeys(List<String> v) { this.roleKeys = v; return this; }
}
