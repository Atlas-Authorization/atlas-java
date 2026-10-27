package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code POST /v1/org_ldap_connections/{id}/sync_runs}. */
public final class OrgLdapConnectionsSyncRunsCreateBody {
    public String kind;

    public OrgLdapConnectionsSyncRunsCreateBody() {
    }

    public OrgLdapConnectionsSyncRunsCreateBody kind(String v) { this.kind = v; return this; }
}
