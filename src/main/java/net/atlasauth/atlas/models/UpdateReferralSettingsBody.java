package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Body for {@code PATCH /v1/instance/referrals}: only the fields set change. Clear a nullable
 * field with {@link #clear(String)} (sent as {@code null}). Enabling without what the provider
 * needs is 422 {@code REFERRALS_NOT_CONFIGURED}.
 */
public final class UpdateReferralSettingsBody {
    public Boolean enabled;
    public String provider;
    /** https only (http only for localhost on a development instance). */
    public String endpointUrl;
    public String programId;
    public String connectionId;
    /** Write-only, 16-512 characters. */
    public String signingSecret;
    /** Write-only vendor API key. */
    public String apiKey;
    /**
     * Write-only, 16-512 characters: verifies the platform's events to {@code inbound.url}
     * (two-way sync). Optional for the Atlas referral protocol (the signing secret is used when unset).
     */
    public String inboundSecret;
    /** The platform's id for Atlas's event subscription, kept for reference. */
    public String inboundEndpointId;
    public List<String> urlParams;
    public String contextCookie;
    public String consentCategory;
    public String consentFallbackCategory;
    public Boolean autoEnrollUsers;
    /** An https URL containing {@code {code}}. */
    public String linkTemplate;
    /** 100-5000. */
    public Integer timeoutMs;
    public UpdateReferralSecurityBody security;
    /** 1-128 of A-Z a-z 0-9 _ -. */
    public String trackingToken;
    /** An https URL template containing {@code {code}}. */
    public String codeLookupUrl;
    public Boolean signupCodeField;
    /**
     * The referral programs whose script or cookie the browser reads, in order (referally,
     * rewardful, tolt, promotekit, firstpromoter, partnerstack, dub). {@code clear("browser_sources")}
     * goes back to the provider's default.
     */
    public List<String> browserSources;
    /** auto, one consent manager (cookiemunch, onetrust, cookiebot, didomi, usercentrics, osano, cookieyes, complianz, klaro, tcf, consent_mode), or app. */
    public String consentProvider;
    public Boolean forwardLifecycle;
    public Boolean mirrorToPublicMetadata;
    public Boolean includeReferralClaims;

    @JsonIgnore
    private final Map<String, Object> additionalFields = new LinkedHashMap<>();

    public UpdateReferralSettingsBody() {
    }

    public UpdateReferralSettingsBody enabled(Boolean v) { this.enabled = v; return this; }
    public UpdateReferralSettingsBody provider(String v) { this.provider = v; return this; }
    public UpdateReferralSettingsBody endpointUrl(String v) { this.endpointUrl = v; return this; }
    public UpdateReferralSettingsBody programId(String v) { this.programId = v; return this; }
    public UpdateReferralSettingsBody connectionId(String v) { this.connectionId = v; return this; }
    public UpdateReferralSettingsBody signingSecret(String v) { this.signingSecret = v; return this; }
    public UpdateReferralSettingsBody apiKey(String v) { this.apiKey = v; return this; }
    public UpdateReferralSettingsBody inboundSecret(String v) { this.inboundSecret = v; return this; }
    public UpdateReferralSettingsBody inboundEndpointId(String v) { this.inboundEndpointId = v; return this; }
    public UpdateReferralSettingsBody urlParams(List<String> v) { this.urlParams = v; return this; }
    public UpdateReferralSettingsBody contextCookie(String v) { this.contextCookie = v; return this; }
    public UpdateReferralSettingsBody consentCategory(String v) { this.consentCategory = v; return this; }
    public UpdateReferralSettingsBody consentFallbackCategory(String v) { this.consentFallbackCategory = v; return this; }
    public UpdateReferralSettingsBody autoEnrollUsers(Boolean v) { this.autoEnrollUsers = v; return this; }
    public UpdateReferralSettingsBody linkTemplate(String v) { this.linkTemplate = v; return this; }
    public UpdateReferralSettingsBody timeoutMs(Integer v) { this.timeoutMs = v; return this; }
    public UpdateReferralSettingsBody security(UpdateReferralSecurityBody v) { this.security = v; return this; }
    public UpdateReferralSettingsBody trackingToken(String v) { this.trackingToken = v; return this; }
    public UpdateReferralSettingsBody codeLookupUrl(String v) { this.codeLookupUrl = v; return this; }
    public UpdateReferralSettingsBody signupCodeField(Boolean v) { this.signupCodeField = v; return this; }
    public UpdateReferralSettingsBody browserSources(List<String> v) { this.browserSources = v; return this; }
    public UpdateReferralSettingsBody consentProvider(String v) { this.consentProvider = v; return this; }
    public UpdateReferralSettingsBody forwardLifecycle(Boolean v) { this.forwardLifecycle = v; return this; }
    public UpdateReferralSettingsBody mirrorToPublicMetadata(Boolean v) { this.mirrorToPublicMetadata = v; return this; }
    public UpdateReferralSettingsBody includeReferralClaims(Boolean v) { this.includeReferralClaims = v; return this; }

    /**
     * Clear a nullable field by its wire name ({@code endpoint_url}, {@code program_id},
     * {@code connection_id}, {@code signing_secret}, {@code api_key}, {@code inbound_secret},
     * {@code inbound_endpoint_id}, {@code context_cookie},
     * {@code consent_fallback_category}, {@code link_template}, {@code tracking_token},
     * {@code code_lookup_url}, {@code browser_sources}): it is sent as {@code null}.
     */
    public UpdateReferralSettingsBody clear(String key) { this.additionalFields.put(key, null); return this; }

    /** Set any field by its wire name, including one this SDK does not model yet. */
    public UpdateReferralSettingsBody set(String key, Object value) { this.additionalFields.put(key, value); return this; }

    @JsonAnyGetter
    public Map<String, Object> additionalFields() { return additionalFields; }
}
