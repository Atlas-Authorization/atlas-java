package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.AnalyticsReportsAccessByAppResponse;
import net.atlasauth.atlas.models.AnalyticsReportsInactiveUsersResponse;
import net.atlasauth.atlas.models.AnalyticsReportsMfaCoverageResponse;

import java.util.List;
import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** The {@code /v1/analytics} resource. */
public final class AnalyticsReports {

    private final Transport t;

    public AnalyticsReports(Transport t) {
        this.t = t;
    }

    /** {@code GET /v1/analytics/reports/inactive_users} - Compliance report: users who have not signed in for `inactive_days` (default 90), oldest id first, cursor-paginated (an export returns up to 50,000 rows). Scope: {@code analytics:read}. */
    public AnalyticsReportsInactiveUsersResponse inactiveUsers() {
        return inactiveUsers(null, null, null, null, null, null, null, null, null);
    }

    /** As above, with every optional parameter. */
    public AnalyticsReportsInactiveUsersResponse inactiveUsers(Long inactiveDays, Long limit, String startingAfter, String from, String to, Long days, String granularity, String organizationId, String format) {
        Map<String, Object> q = Params.of().set("inactive_days", inactiveDays).set("limit", limit).set("starting_after", startingAfter).set("from", from).set("to", to).set("days", days).set("granularity", granularity).set("organization_id", organizationId).set("format", format).map();
        return t.request("GET", "/v1/analytics/reports/inactive_users", q, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/analytics/reports/mfa_coverage} - Compliance report: how many users are protected by a second factor, a passkey, either, or neither. Scope: {@code analytics:read}. */
    public AnalyticsReportsMfaCoverageResponse mfaCoverage() {
        return mfaCoverage(null, null, null, null, null, null, null, null);
    }

    /** As above, with every optional parameter. */
    public AnalyticsReportsMfaCoverageResponse mfaCoverage(String from, String to, Long days, String granularity, String organizationId, String format, Long limit, String startingAfter) {
        Map<String, Object> q = Params.of().set("from", from).set("to", to).set("days", days).set("granularity", granularity).set("organization_id", organizationId).set("format", format).set("limit", limit).set("starting_after", startingAfter).map();
        return t.request("GET", "/v1/analytics/reports/mfa_coverage", q, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/analytics/reports/access_by_app} - Compliance report: for each application (OAuth client), how many users have access, how many grants are active and how many users signed in during the window. Scope: {@code analytics:read}. */
    public AnalyticsReportsAccessByAppResponse accessByApp() {
        return accessByApp(null, null, null, null, null, null, null, null);
    }

    /** As above, with every optional parameter. */
    public AnalyticsReportsAccessByAppResponse accessByApp(String from, String to, Long days, String granularity, String organizationId, String format, Long limit, String startingAfter) {
        Map<String, Object> q = Params.of().set("from", from).set("to", to).set("days", days).set("granularity", granularity).set("organization_id", organizationId).set("format", format).set("limit", limit).set("starting_after", startingAfter).map();
        return t.request("GET", "/v1/analytics/reports/access_by_app", q, null, null, false, new TypeReference<>() {
        });
    }
}
