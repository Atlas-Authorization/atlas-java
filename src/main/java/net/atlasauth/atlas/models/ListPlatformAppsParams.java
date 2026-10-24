package net.atlasauth.atlas.models;

import net.atlasauth.atlas.Params;

import java.util.Map;

/** Query parameters; unset values are omitted. */
public final class ListPlatformAppsParams {
    public Boolean includeArchived;
    public Boolean archived;
    public String tag;
    /** Keeps only apps whose provisioning metadata matches every pair ({@code metadata[key]=value}). */
    public Map<String, Object> metadata;

    public ListPlatformAppsParams() {
    }

    public ListPlatformAppsParams includeArchived(Boolean v) { this.includeArchived = v; return this; }
    public ListPlatformAppsParams archived(Boolean v) { this.archived = v; return this; }
    public ListPlatformAppsParams tag(String v) { this.tag = v; return this; }
    public ListPlatformAppsParams metadata(Map<String, Object> v) { this.metadata = v; return this; }

    /** The query map the transport serializes. */
    public Map<String, Object> toQuery() {
        Params p = Params.of()
                .set("include_archived", includeArchived)
                .set("archived", archived)
                .set("tag", tag);
        Params.putMetadata(p.map(), metadata);
        return p.map();
    }
}
