package net.atlasauth.atlas.models;

import net.atlasauth.atlas.Params;

import java.util.Map;

/** Query parameters; unset values are omitted. */
public final class ListEndUsersOrganizationsParams {
    public String status;
    public Long limit;
    public String startingAfter;

    public ListEndUsersOrganizationsParams() {
    }

    public ListEndUsersOrganizationsParams status(String v) { this.status = v; return this; }
    public ListEndUsersOrganizationsParams limit(Long v) { this.limit = v; return this; }
    public ListEndUsersOrganizationsParams startingAfter(String v) { this.startingAfter = v; return this; }

    /** The query map the transport serializes. */
    public Map<String, Object> toQuery() {
        return Params.of()
                .set("status", status)
                .set("limit", limit)
                .set("starting_after", startingAfter)
                .map();
    }
}
