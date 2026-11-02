package net.atlasauth.atlas.models;

import net.atlasauth.atlas.Params;

import java.util.Map;

/** Query parameters for {@code GET /v1/organizations}; unset values are omitted. */
public final class ListOrganizationsParams {
    public Integer limit;
    public String startingAfter;
    /** Only the organization with this external id. */
    public String externalId;
    /** {@code public_metadata} equality filters, sent as {@code metadata[key]=value} (all must match). */
    public Map<String, Object> metadata;

    public ListOrganizationsParams() {
    }

    public ListOrganizationsParams limit(Integer v) { this.limit = v; return this; }
    public ListOrganizationsParams startingAfter(String v) { this.startingAfter = v; return this; }
    public ListOrganizationsParams externalId(String v) { this.externalId = v; return this; }
    public ListOrganizationsParams metadata(Map<String, Object> v) { this.metadata = v; return this; }

    /** The query map the transport serializes. */
    public Map<String, Object> toQuery() {
        Params p = Params.of()
                .set("limit", limit)
                .set("starting_after", startingAfter)
                .set("external_id", externalId);
        Params.putMetadata(p.map(), metadata);
        return p.map();
    }
}
