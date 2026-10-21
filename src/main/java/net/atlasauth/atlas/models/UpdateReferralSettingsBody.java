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
    public UpdateReferralSettingsBody urlParams(List<String> v) { this.urlParams = v; return this; }
    public UpdateReferralSettingsBody contextCookie(String v) { this.contextCookie = v; return this; }
    public UpdateReferralSettingsBody consentCategory(String v) { this.consentCategory = v; return this; }
    public UpdateReferralSettingsBody consentFallbackCategory(String v) { this.consentFallbackCategory = v; return this; }
    public UpdateReferralSettingsBody autoEnrollUsers(Boolean v) { this.autoEnrollUsers = v; return this; }
    public UpdateReferralSettingsBody linkTemplate(String v) { this.linkTemplate = v; return this; }
    public UpdateReferralSettingsBody timeoutMs(Integer v) { this.timeoutMs = v; return this; }
    public UpdateReferralSettingsBody security(UpdateReferralSecurityBody v) { this.security = v; return this; }

    /**
     * Clear a nullable field by its wire name ({@code endpoint_url}, {@code program_id},
     * {@code connection_id}, {@code signing_secret}, {@code api_key}, {@code context_cookie},
     * {@code consent_fallback_category}, {@code link_template}): it is sent as {@code null}.
     */
    public UpdateReferralSettingsBody clear(String key) { this.additionalFields.put(key, null); return this; }

    /** Set any field by its wire name, including one this SDK does not model yet. */
    public UpdateReferralSettingsBody set(String key, Object value) { this.additionalFields.put(key, value); return this; }

    @JsonAnyGetter
    public Map<String, Object> additionalFields() { return additionalFields; }
}
