package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.CursorPage;
import net.atlasauth.atlas.models.Event;
import net.atlasauth.atlas.models.ListEventsParams;
import net.atlasauth.atlas.models.Usage;

import java.util.List;
import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** Usage analytics ({@code /v1/usage}) and the product activity feed ({@code /v1/events}). */
public final class Analytics {

    private final Transport t;

    public Analytics(Transport t) {
        this.t = t;
    }

    public Usage usage() {
        return usage(null);
    }

    /** {@code GET /v1/usage} — Read usage analytics: daily sign-in/active-user series with peaks and trends over a window, plus monthly active users. */
    public Usage usage(Long days) {
        Map<String, Object> q = Params.of().set("days", days).map();
        return t.get("/v1/usage", q, new TypeReference<>() {
        });
    }

    public CursorPage<Event> events() {
        return events(null);
    }

    /** {@code GET /v1/events} — Read the product activity feed (events), filterable by type and time and cursor-paginated, with the list of event types this instance has actually emitted. */
    public CursorPage<Event> events(ListEventsParams params) {
        return t.get("/v1/events", params == null ? null : params.toQuery(), new TypeReference<>() {
        });
    }
}
