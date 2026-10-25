package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code PUT /v1/instance/integrations/{id}}: connect or replace an identity integration (a preset - chatly, forumulate, cookiemunch, referalli - or a custom one). {@link #clear(String...)} sends a nullable field as null: events_url, backchannel_logout_url, verify_error_report_url, cmp, audience_format, principal_claim, ext_claim, external_ref, per_call_audience. */
public final class SetIntegrationBody extends ExtensibleBody {
    public Boolean enabled;
    /** {@code <preset>:<id>} for a preset; any value for a custom one. */
    public String audience;
    /** Display name (required for a custom integration). */
    public String name;
    /** support_chat, docs_community, consent, referrals or other. */
    public String role;
    public String audienceFormat;
    public String principalClaim;
    public String extClaim;
    /** 10-120 (default 120). */
    public Integer ttlSeconds;
    public String eventsUrl;
    public String backchannelLogoutUrl;
    public String verifyErrorReportUrl;
    /** The consent-manager block: script_url, script_id, site_id, blocking_mode, extra_origins, settings_preset. */
    public Map<String, Object> cmp;
    public Boolean atlasIdUsers;
    /**
     * Let a token be minted for a per-call audience: {@code param} (the query parameter that carries it),
     * {@code template} (the audience with the value filled in), {@code allowed} (the values permitted) and
     * {@code any_matching}. {@link #clear(String...)} with {@code per_call_audience} turns it off.
     */
    public Map<String, Object> perCallAudience;
    public String externalRef;
    /** Chatly: the channel public id. */
    public String channel;
    /** Forumulate / referalli: the organization id. */
    public String orgId;
    /** Forumulate: spaces ({@code id}, {@code redeem_url}). */
    public List<Map<String, Object>> spaces;

    public SetIntegrationBody() {
    }

    public SetIntegrationBody enabled(Boolean v) { this.enabled = v; return this; }
    public SetIntegrationBody audience(String v) { this.audience = v; return this; }
    public SetIntegrationBody name(String v) { this.name = v; return this; }
    public SetIntegrationBody role(String v) { this.role = v; return this; }
    public SetIntegrationBody audienceFormat(String v) { this.audienceFormat = v; return this; }
    public SetIntegrationBody principalClaim(String v) { this.principalClaim = v; return this; }
    public SetIntegrationBody extClaim(String v) { this.extClaim = v; return this; }
    public SetIntegrationBody ttlSeconds(Integer v) { this.ttlSeconds = v; return this; }
    public SetIntegrationBody eventsUrl(String v) { this.eventsUrl = v; return this; }
    public SetIntegrationBody backchannelLogoutUrl(String v) { this.backchannelLogoutUrl = v; return this; }
    public SetIntegrationBody verifyErrorReportUrl(String v) { this.verifyErrorReportUrl = v; return this; }
    public SetIntegrationBody cmp(Map<String, Object> v) { this.cmp = v; return this; }
    public SetIntegrationBody atlasIdUsers(Boolean v) { this.atlasIdUsers = v; return this; }
    public SetIntegrationBody perCallAudience(Map<String, Object> v) { this.perCallAudience = v; return this; }
    public SetIntegrationBody externalRef(String v) { this.externalRef = v; return this; }
    public SetIntegrationBody channel(String v) { this.channel = v; return this; }
    public SetIntegrationBody orgId(String v) { this.orgId = v; return this; }
    public SetIntegrationBody spaces(List<Map<String, Object>> v) { this.spaces = v; return this; }

    /** Send the named nullable fields (wire names) as {@code null}, clearing the stored value. */
    public SetIntegrationBody clear(String... keys) { putNulls(keys); return this; }

    /** Set any field by its wire name, including one this SDK does not model yet. */
    public SetIntegrationBody set(String key, Object value) { putField(key, value); return this; }

}
