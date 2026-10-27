package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code POST /v1/fga/stores/{id}/list-users}. */
public final class FgaListUsersBody {
    public Object object;
    public String relation;
    public List<Map<String, Object>> userFilters;
    public String authorizationModelId;
    public Object contextualTuples;
    public Map<String, Object> context;
    public Long pageSize;
    public String continuationToken;
    public String consistency;
    public String consistencyToken;

    public FgaListUsersBody() {
    }

    public FgaListUsersBody object(Object v) { this.object = v; return this; }
    public FgaListUsersBody relation(String v) { this.relation = v; return this; }
    public FgaListUsersBody userFilters(List<Map<String, Object>> v) { this.userFilters = v; return this; }
    public FgaListUsersBody authorizationModelId(String v) { this.authorizationModelId = v; return this; }
    public FgaListUsersBody contextualTuples(Object v) { this.contextualTuples = v; return this; }
    public FgaListUsersBody context(Map<String, Object> v) { this.context = v; return this; }
    public FgaListUsersBody pageSize(Long v) { this.pageSize = v; return this; }
    public FgaListUsersBody continuationToken(String v) { this.continuationToken = v; return this; }
    public FgaListUsersBody consistency(String v) { this.consistency = v; return this; }
    public FgaListUsersBody consistencyToken(String v) { this.consistencyToken = v; return this; }
}
