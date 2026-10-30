package net.atlasauth.atlas.models;

import java.util.List;
import java.util.Map;

/**
 * Body for {@code POST /v1/instance/privacy_integrations/connect}: wires a consent manager / orchestrator both
 * ways in one idempotent call.
 */
public final class PrivacyIntegrationConnectBody extends ExtensibleBody {
    /** An orchestrator preset ({@code cookiemunch}) or {@code custom}. */
    public String preset;
    /** The orchestrator-side site id. Required by the cookiemunch preset. */
    public String siteId;
    /** The key the orchestrator issued to Atlas for opening cases (optional on a re-run). Write-only. */
    public String apiKey;
    public String partnerLinkId;
    /** The webhook URL the orchestrator shows for this instance's executor connection (optional on a re-run). */
    public String completionWebhookUrl;
    /** Overrides for {@code PUT /v1/instance/cookie_consent} (merged over the preset), or {@code Boolean.FALSE} to leave it alone. */
    public Object cookieConsent;
    /** Further orchestrator fields (name, case_url, cancel_url, sign_requests, ...). */
    public Map<String, Object> orchestrator;
    /** The executor key: privacy:* scopes only (default privacy:execute + privacy:grants:revoke). */
    public Key key;

    public PrivacyIntegrationConnectBody() {
    }

    public PrivacyIntegrationConnectBody(String preset) {
        this.preset = preset;
    }

    public PrivacyIntegrationConnectBody preset(String v) { this.preset = v; return this; }
    public PrivacyIntegrationConnectBody siteId(String v) { this.siteId = v; return this; }
    public PrivacyIntegrationConnectBody apiKey(String v) { this.apiKey = v; return this; }
    public PrivacyIntegrationConnectBody partnerLinkId(String v) { this.partnerLinkId = v; return this; }
    public PrivacyIntegrationConnectBody completionWebhookUrl(String v) { this.completionWebhookUrl = v; return this; }
    public PrivacyIntegrationConnectBody cookieConsent(Map<String, Object> v) { this.cookieConsent = v; return this; }
    /** Leave the cookie consent settings alone ({@code cookie_consent: false}). */
    public PrivacyIntegrationConnectBody keepCookieConsent() { this.cookieConsent = Boolean.FALSE; return this; }
    public PrivacyIntegrationConnectBody orchestrator(Map<String, Object> v) { this.orchestrator = v; return this; }
    public PrivacyIntegrationConnectBody key(Key v) { this.key = v; return this; }
    public PrivacyIntegrationConnectBody set(String key, Object value) { putField(key, value); return this; }

    /** The executor key settings. */
    public static final class Key {
        public String name;
        public List<String> scopes;
        /** Epoch ms or an ISO 8601 string. */
        public Object expiresAt;
        public List<String> allowedIps;
        public List<String> allowedOrigins;

        public Key() {
        }

        public Key name(String v) { this.name = v; return this; }
        public Key scopes(List<String> v) { this.scopes = v; return this; }
        public Key expiresAt(Object v) { this.expiresAt = v; return this; }
        public Key allowedIps(List<String> v) { this.allowedIps = v; return this; }
        public Key allowedOrigins(List<String> v) { this.allowedOrigins = v; return this; }
    }
}
