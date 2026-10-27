package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code PATCH /v1/analytics/alert_rules/{id}}. */
public final class AnalyticsAlertRulesUpdateBody {
    public String name;
    /** Percent metrics are 0-100; failed_sign_ins is a count; sign_in_p95_seconds is seconds; api_usage_spike is the ratio of a key or organization's request rate in the window to its average hourly rate over the 7 days before (for example 10 = ten times its usual volume), and min_sample is the fewest requ */
    public String metric;
    public String comparator;
    public Double threshold;
    /** Trailing window, 5 to 1440 (default 60). */
    public Long windowMinutes;
    /** Ratios and latency are not evaluated on fewer observations (default 20). */
    public Long minSample;
    /** Minimum minutes between notifications while the rule stays firing (default 60). */
    public Long cooldownMinutes;
    public List<String> emailContacts;
    /** A public HTTPS endpoint. The body is signed like any Atlas webhook; the signing secret is returned once, on create. */
    public String webhookUrl;
    public Boolean enabled;
    /** api_usage_spike only: watch secret keys, end-user API keys or organizations (null = all). */
    public String subjectType;
    /** api_usage_spike only: watch one key or organization (needs subject_type). */
    public String subjectId;
    /** Also raise a Shared Signals event (an opaque subject naming the key or organization) on every enabled stream that asks for it. */
    public Boolean ssf;

    public AnalyticsAlertRulesUpdateBody() {
    }

    public AnalyticsAlertRulesUpdateBody name(String v) { this.name = v; return this; }
    public AnalyticsAlertRulesUpdateBody metric(String v) { this.metric = v; return this; }
    public AnalyticsAlertRulesUpdateBody comparator(String v) { this.comparator = v; return this; }
    public AnalyticsAlertRulesUpdateBody threshold(Double v) { this.threshold = v; return this; }
    public AnalyticsAlertRulesUpdateBody windowMinutes(Long v) { this.windowMinutes = v; return this; }
    public AnalyticsAlertRulesUpdateBody minSample(Long v) { this.minSample = v; return this; }
    public AnalyticsAlertRulesUpdateBody cooldownMinutes(Long v) { this.cooldownMinutes = v; return this; }
    public AnalyticsAlertRulesUpdateBody emailContacts(List<String> v) { this.emailContacts = v; return this; }
    public AnalyticsAlertRulesUpdateBody webhookUrl(String v) { this.webhookUrl = v; return this; }
    public AnalyticsAlertRulesUpdateBody enabled(Boolean v) { this.enabled = v; return this; }
    public AnalyticsAlertRulesUpdateBody subjectType(String v) { this.subjectType = v; return this; }
    public AnalyticsAlertRulesUpdateBody subjectId(String v) { this.subjectId = v; return this; }
    public AnalyticsAlertRulesUpdateBody ssf(Boolean v) { this.ssf = v; return this; }
}
