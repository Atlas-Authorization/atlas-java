package net.atlasauth.atlas.models;

import net.atlasauth.atlas.Params;

import java.util.Map;

/** Query parameters; unset values are omitted. */
public final class ListPlatformAppsParams {
    public Boolean includeArchived;
    public Boolean archived;
    public String tag;

    public ListPlatformAppsParams() {
    }

    public ListPlatformAppsParams includeArchived(Boolean v) { this.includeArchived = v; return this; }
    public ListPlatformAppsParams archived(Boolean v) { this.archived = v; return this; }
    public ListPlatformAppsParams tag(String v) { this.tag = v; return this; }

    /** The query map the transport serializes. */
    public Map<String, Object> toQuery() {
        return Params.of()
                .set("include_archived", includeArchived)
                .set("archived", archived)
                .set("tag", tag)
                .map();
    }
}
