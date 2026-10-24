package net.atlasauth.atlas.models;

import net.atlasauth.atlas.Params;

import java.util.Map;

/** Query parameters; unset values are omitted. */
public final class ListConfigChangelogParams {
    public String action;
    public String since;
    public Long limit;
    public String startingAfter;

    /** Also list writes that changed nothing (default false). */
    public Boolean includeNoOp;

    public ListConfigChangelogParams() {
    }

    public ListConfigChangelogParams action(String v) { this.action = v; return this; }
    public ListConfigChangelogParams since(String v) { this.since = v; return this; }
    public ListConfigChangelogParams limit(Long v) { this.limit = v; return this; }
    public ListConfigChangelogParams startingAfter(String v) { this.startingAfter = v; return this; }

    public ListConfigChangelogParams includeNoOp(Boolean v) { this.includeNoOp = v; return this; }

    /** The query map the transport serializes. */
    public Map<String, Object> toQuery() {
        return Params.of()
                .set("action", action)
                .set("since", since)
                .set("limit", limit)
                .set("starting_after", startingAfter)
                .set("include_no_op", includeNoOp)
                .map();
    }
}
