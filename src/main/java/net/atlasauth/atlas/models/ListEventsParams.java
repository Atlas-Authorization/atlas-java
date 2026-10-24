package net.atlasauth.atlas.models;

import net.atlasauth.atlas.Params;

import java.util.Map;

/** Query parameters; unset values are omitted. */
public final class ListEventsParams {
    public String type;
    public String from;
    public String to;
    public Long limit;
    public String startingAfter;

    /** ISO 8601 or epoch ms: only events at or after this moment. */
    public String since;
    /** ISO 8601 or epoch ms: only events before this moment. */
    public String until;
    /** {@code asc} or {@code desc}. */
    public String order;

    public ListEventsParams() {
    }

    public ListEventsParams type(String v) { this.type = v; return this; }
    public ListEventsParams from(String v) { this.from = v; return this; }
    public ListEventsParams to(String v) { this.to = v; return this; }
    public ListEventsParams limit(Long v) { this.limit = v; return this; }
    public ListEventsParams startingAfter(String v) { this.startingAfter = v; return this; }

    public ListEventsParams since(String v) { this.since = v; return this; }
    public ListEventsParams until(String v) { this.until = v; return this; }
    public ListEventsParams order(String v) { this.order = v; return this; }

    /** The query map the transport serializes. */
    public Map<String, Object> toQuery() {
        return Params.of()
                .set("type", type)
                .set("from", from)
                .set("to", to)
                .set("limit", limit)
                .set("starting_after", startingAfter)
                .set("since", since)
                .set("until", until)
                .set("order", order)
                .map();
    }
}
