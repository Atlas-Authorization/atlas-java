package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code PATCH /v1/grant_management/grants/{id}}. */
public final class ManagedGrantsNarrowBody {
    public List<String> scopes;
    public List<Map<String, Object>> authorizationDetails;

    public ManagedGrantsNarrowBody() {
    }

    public ManagedGrantsNarrowBody scopes(List<String> v) { this.scopes = v; return this; }
    public ManagedGrantsNarrowBody authorizationDetails(List<Map<String, Object>> v) { this.authorizationDetails = v; return this; }
}
