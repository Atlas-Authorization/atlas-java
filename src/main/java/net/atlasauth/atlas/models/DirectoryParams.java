package net.atlasauth.atlas.models;

import net.atlasauth.atlas.Params;

import java.util.Map;

/** Query parameters; unset values are omitted. */
public final class DirectoryParams {
    public String q;
    public String category;
    public Boolean verified;
    public Long limit;
    public Long offset;

    public DirectoryParams() {
    }

    public DirectoryParams q(String v) { this.q = v; return this; }
    public DirectoryParams category(String v) { this.category = v; return this; }
    public DirectoryParams verified(Boolean v) { this.verified = v; return this; }
    public DirectoryParams limit(Long v) { this.limit = v; return this; }
    public DirectoryParams offset(Long v) { this.offset = v; return this; }

    /** The query map the transport serializes. */
    public Map<String, Object> toQuery() {
        return Params.of()
                .set("q", q)
                .set("category", category)
                .set("verified", verified)
                .set("limit", limit)
                .set("offset", offset)
                .map();
    }
}
