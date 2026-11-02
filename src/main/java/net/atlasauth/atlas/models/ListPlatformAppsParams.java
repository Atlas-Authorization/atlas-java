package net.atlasauth.atlas.models;

import net.atlasauth.atlas.Params;

import java.util.Map;

/** Query parameters; unset values are omitted. */
public final class ListPlatformAppsParams {
    public Boolean includeArchived;
    public Boolean archived;
    public String tag;
    /** Only apps with exactly this name, ignoring case. */
    public String name;
    /** Only apps whose name starts with this text, ignoring case. */
    public String namePrefix;
    /** Only the app created with this {@code external_ref} (zero or one item). */
    public String externalRef;
    /** Page size (up to 500). Without it every matching app is returned. */
    public Integer limit;
    /** The previous page's {@code nextCursor}. */
    public String startingAfter;
    /** Keeps only apps whose provisioning metadata matches every pair ({@code metadata[key]=value}). */
    public Map<String, Object> metadata;

    public ListPlatformAppsParams() {
    }

    public ListPlatformAppsParams includeArchived(Boolean v) { this.includeArchived = v; return this; }
    public ListPlatformAppsParams archived(Boolean v) { this.archived = v; return this; }
    public ListPlatformAppsParams tag(String v) { this.tag = v; return this; }
    public ListPlatformAppsParams name(String v) { this.name = v; return this; }
    public ListPlatformAppsParams namePrefix(String v) { this.namePrefix = v; return this; }
    public ListPlatformAppsParams externalRef(String v) { this.externalRef = v; return this; }
    public ListPlatformAppsParams limit(Integer v) { this.limit = v; return this; }
    public ListPlatformAppsParams startingAfter(String v) { this.startingAfter = v; return this; }
    public ListPlatformAppsParams metadata(Map<String, Object> v) { this.metadata = v; return this; }

    /** The query map the transport serializes. */
    public Map<String, Object> toQuery() {
        Params p = Params.of()
                .set("include_archived", includeArchived)
                .set("archived", archived)
                .set("tag", tag)
                .set("name", name)
                .set("name_prefix", namePrefix)
                .set("external_ref", externalRef)
                .set("limit", limit)
                .set("starting_after", startingAfter);
        Params.putMetadata(p.map(), metadata);
        return p.map();
    }
}
