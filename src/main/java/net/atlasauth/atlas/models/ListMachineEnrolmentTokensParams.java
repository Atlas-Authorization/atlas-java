package net.atlasauth.atlas.models;

import net.atlasauth.atlas.Params;

import java.util.Map;

/** Query parameters; unset values are omitted. */
public final class ListMachineEnrolmentTokensParams {
    public String organizationId;
    public Long limit;
    public String startingAfter;

    public ListMachineEnrolmentTokensParams() {
    }

    public ListMachineEnrolmentTokensParams organizationId(String v) { this.organizationId = v; return this; }
    public ListMachineEnrolmentTokensParams limit(Long v) { this.limit = v; return this; }
    public ListMachineEnrolmentTokensParams startingAfter(String v) { this.startingAfter = v; return this; }

    /** The query map the transport serializes. */
    public Map<String, Object> toQuery() {
        return Params.of()
                .set("organization_id", organizationId)
                .set("limit", limit)
                .set("starting_after", startingAfter)
                .map();
    }
}
