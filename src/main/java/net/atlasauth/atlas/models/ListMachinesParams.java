package net.atlasauth.atlas.models;

import net.atlasauth.atlas.Params;

import java.util.Map;

/** Query parameters; unset values are omitted. */
public final class ListMachinesParams {
    public String status;
    public String hostname;
    public String ownerUserId;
    public String organizationId;
    public String metadata;
    public Long limit;
    public String startingAfter;

    public ListMachinesParams() {
    }

    public ListMachinesParams status(String v) { this.status = v; return this; }
    public ListMachinesParams hostname(String v) { this.hostname = v; return this; }
    public ListMachinesParams ownerUserId(String v) { this.ownerUserId = v; return this; }
    public ListMachinesParams organizationId(String v) { this.organizationId = v; return this; }
    public ListMachinesParams metadata(String v) { this.metadata = v; return this; }
    public ListMachinesParams limit(Long v) { this.limit = v; return this; }
    public ListMachinesParams startingAfter(String v) { this.startingAfter = v; return this; }

    /** The query map the transport serializes. */
    public Map<String, Object> toQuery() {
        return Params.of()
                .set("status", status)
                .set("hostname", hostname)
                .set("owner_user_id", ownerUserId)
                .set("organization_id", organizationId)
                .set("metadata", metadata)
                .set("limit", limit)
                .set("starting_after", startingAfter)
                .map();
    }
}
