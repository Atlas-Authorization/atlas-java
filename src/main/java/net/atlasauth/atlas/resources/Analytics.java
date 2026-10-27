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
import net.atlasauth.atlas.models.AnalyticsActiveUsersResponse;
import net.atlasauth.atlas.models.AnalyticsApiUsageResponse;
import net.atlasauth.atlas.models.AnalyticsBreakdownResponse;
import net.atlasauth.atlas.models.AnalyticsFailuresResponse;
import net.atlasauth.atlas.models.AnalyticsFunnelResponse;
import net.atlasauth.atlas.models.AnalyticsJourneyFunnelResponse;
import net.atlasauth.atlas.models.AnalyticsPasskeyAdoptionResponse;

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

    /** {@code GET /v1/analytics/funnel} - Where sign-in or sign-up attempts end, per method: started, completed, abandoned, and the step each unfinished attempt stopped at, with the completion rate. Each attempt is counted once, at its final state. Scope: {@code analytics:read}. */
    public AnalyticsFunnelResponse funnel() {
        return funnel(null, null, null, null, null, null, null, null, null);
    }

    /** As above, with every optional parameter. */
    public AnalyticsFunnelResponse funnel(String kind, String from, String to, Long days, String granularity, String organizationId, String format, Long limit, String startingAfter) {
        Map<String, Object> q = Params.of().set("kind", kind).set("from", from).set("to", to).set("days", days).set("granularity", granularity).set("organization_id", organizationId).set("format", format).set("limit", limit).set("starting_after", startingAfter).map();
        return t.request("GET", "/v1/analytics/funnel", q, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/analytics/journey_funnel} - What happened at each step of a sign-in journey: how often it held a sign-in (a required action or form), was completed, ran at the sign-in (an Action, claim or redirect) or refused it, with the completion rate of its holds. Steps follow the active version; a sub-flow's steps are prefixed with the sub-flow step id. No organization slice. Scope: {@code analytics:read}. */
    public AnalyticsJourneyFunnelResponse journeyFunnel(String journeyId) {
        return journeyFunnel(journeyId, null, null, null, null, null, null, null, null);
    }

    /** As above, with every optional parameter. */
    public AnalyticsJourneyFunnelResponse journeyFunnel(String journeyId, String from, String to, Long days, String granularity, String organizationId, String format, Long limit, String startingAfter) {
        Map<String, Object> q = Params.of().set("journey_id", journeyId).set("from", from).set("to", to).set("days", days).set("granularity", granularity).set("organization_id", organizationId).set("format", format).set("limit", limit).set("starting_after", startingAfter).map();
        return t.request("GET", "/v1/analytics/journey_funnel", q, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/analytics/failures} - Failed first- and second-factor attempts by reason and method, most frequent first. No organization slice (a failed attempt belongs to no organization). Scope: {@code analytics:read}. */
    public AnalyticsFailuresResponse failures() {
        return failures(null, null, null, null, null, null, null, null);
    }

    /** As above, with every optional parameter. */
    public AnalyticsFailuresResponse failures(String from, String to, Long days, String granularity, String organizationId, String format, Long limit, String startingAfter) {
        Map<String, Object> q = Params.of().set("from", from).set("to", to).set("days", days).set("granularity", granularity).set("organization_id", organizationId).set("format", format).set("limit", limit).set("starting_after", startingAfter).map();
        return t.request("GET", "/v1/analytics/failures", q, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/analytics/breakdown} - Sign-ins split by one dimension: method, device type, browser, operating system or country (country needs a GeoIP database on the deployment; otherwise it reports `unknown`). Scope: {@code analytics:read}. */
    public AnalyticsBreakdownResponse breakdown() {
        return breakdown(null, null, null, null, null, null, null, null, null);
    }

    /** As above, with every optional parameter. */
    public AnalyticsBreakdownResponse breakdown(String dimension, String from, String to, Long days, String granularity, String organizationId, String format, Long limit, String startingAfter) {
        Map<String, Object> q = Params.of().set("dimension", dimension).set("from", from).set("to", to).set("days", days).set("granularity", granularity).set("organization_id", organizationId).set("format", format).set("limit", limit).set("starting_after", startingAfter).map();
        return t.request("GET", "/v1/analytics/breakdown", q, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/analytics/active_users} - Distinct active users and sign-ins per bucket, each bucket split into new users (signed up in that bucket) and returning users, plus trailing 1/7/30-day active-user counts (dau, wau, mau) ending at `to`. Scope: {@code analytics:read}. */
    public AnalyticsActiveUsersResponse activeUsers() {
        return activeUsers(null, null, null, null, null, null, null, null);
    }

    /** As above, with every optional parameter. */
    public AnalyticsActiveUsersResponse activeUsers(String from, String to, Long days, String granularity, String organizationId, String format, Long limit, String startingAfter) {
        Map<String, Object> q = Params.of().set("from", from).set("to", to).set("days", days).set("granularity", granularity).set("organization_id", organizationId).set("format", format).set("limit", limit).set("starting_after", startingAfter).map();
        return t.request("GET", "/v1/analytics/active_users", q, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/analytics/passkey_adoption} - How many users hold a passkey (adoption rate), and the passkey share of sign-ins per bucket. Scope: {@code analytics:read}. */
    public AnalyticsPasskeyAdoptionResponse passkeyAdoption() {
        return passkeyAdoption(null, null, null, null, null, null, null, null);
    }

    /** As above, with every optional parameter. */
    public AnalyticsPasskeyAdoptionResponse passkeyAdoption(String from, String to, Long days, String granularity, String organizationId, String format, Long limit, String startingAfter) {
        Map<String, Object> q = Params.of().set("from", from).set("to", to).set("days", days).set("granularity", granularity).set("organization_id", organizationId).set("format", format).set("limit", limit).set("starting_after", startingAfter).map();
        return t.request("GET", "/v1/analytics/passkey_adoption", q, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/analytics/api_usage} - API usage per subject against its own baseline: secret keys calling the Backend API, end-user API keys verified through /v1/api_keys/verify, and organizations (Backend API calls on the organization plus verifications of its keys). For each: requests and the failure rate in the window, the average hourly rate over the 7 days before, and the ratio between them, highest ratio first (at most 100). Counts are written in batches and can lag by up to a minute. Scope: {@code analytics:read}. */
    public AnalyticsApiUsageResponse apiUsage() {
        return apiUsage(null, null, null, null, null);
    }

    /** As above, with every optional parameter. */
    public AnalyticsApiUsageResponse apiUsage(String subjectType, String subjectId, Long windowMinutes, Long limit, String startingAfter) {
        Map<String, Object> q = Params.of().set("subject_type", subjectType).set("subject_id", subjectId).set("window_minutes", windowMinutes).set("limit", limit).set("starting_after", startingAfter).map();
        return t.request("GET", "/v1/analytics/api_usage", q, null, null, false, new TypeReference<>() {
        });
    }

    /** The {@code reports} sub-resource. */
    public AnalyticsReports reports() {
        return new AnalyticsReports(t);
    }

    /** The {@code alertRules} sub-resource. */
    public AnalyticsAlertRules alertRules() {
        return new AnalyticsAlertRules(t);
    }
}
