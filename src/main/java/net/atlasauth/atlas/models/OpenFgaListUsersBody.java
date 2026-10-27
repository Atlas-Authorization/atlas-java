package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code POST /v1/openfga/stores/{store}/list-users}. Required: object, relation, userFilters. */
public final class OpenFgaListUsersBody {
    /** Required. */
    public Map<String, Object> object;
    /** e.g. viewer Required. */
    public String relation;
    /** Required. */
    public List<Map<String, Object>> userFilters;
    public String authorizationModelId;
    /** The OpenFGA tuple-list wrapper. */
    public Map<String, Object> contextualTuples;
    /** Request-time values for tuple-condition parameters. A tuple's own context wins on a clash. */
    public Map<String, Object> context;
    /** MINIMIZE_LATENCY (and UNSPECIFIED) may be answered from a short-lived result cache; HIGHER_CONSISTENCY bypasses it and reads the primary. */
    public String consistency;
    /** A token returned by a write (or any read): evaluate at least that fresh, so a check can never be answered from before that write. Also accepted as the `Atlas-Consistency-Token` request header. */
    public String consistencyToken;
    /** Defaults to 1000. */
    public Long pageSize;
    /** The `continuation_token` of the previous page. */
    public String continuationToken;

    public OpenFgaListUsersBody() {
    }

    public OpenFgaListUsersBody object(Map<String, Object> v) { this.object = v; return this; }
    public OpenFgaListUsersBody relation(String v) { this.relation = v; return this; }
    public OpenFgaListUsersBody userFilters(List<Map<String, Object>> v) { this.userFilters = v; return this; }
    public OpenFgaListUsersBody authorizationModelId(String v) { this.authorizationModelId = v; return this; }
    public OpenFgaListUsersBody contextualTuples(Map<String, Object> v) { this.contextualTuples = v; return this; }
    public OpenFgaListUsersBody context(Map<String, Object> v) { this.context = v; return this; }
    public OpenFgaListUsersBody consistency(String v) { this.consistency = v; return this; }
    public OpenFgaListUsersBody consistencyToken(String v) { this.consistencyToken = v; return this; }
    public OpenFgaListUsersBody pageSize(Long v) { this.pageSize = v; return this; }
    public OpenFgaListUsersBody continuationToken(String v) { this.continuationToken = v; return this; }
}
