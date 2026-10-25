package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code PUT /v1/instance/privacy_orchestrator}. {@link #clear(String...)} sends case_url, cancel_url, api_key, completion_webhook_url, partner_link_id or site_id as null. */
public final class SetPrivacyOrchestratorBody extends ExtensibleBody {
    /** A preset id (cookiemunch, custom) or your own lower-case id. */
    public String provider;
    public String name;
    public Boolean enabled;
    public String caseUrl;
    /** Must contain {@code {id}}. */
    public String cancelUrl;
    public String authHeader;
    /** {@code bearer} or {@code raw}. */
    public String authScheme;
    /** Write-only. */
    public String apiKey;
    public Boolean usePartnerLink;
    /** Call through this partner link (implies {@code usePartnerLink}). */
    public String partnerLinkId;
    /** The orchestrator-side site a case is for. */
    public String siteId;
    /** Cancel a self-scheduled deletion when the person signs back in during the grace period. */
    public Boolean cancelDeletionOnSignIn;
    public Boolean openClosureCases;
    /** true mints (or rotates) a secret and signs every case call; false stops signing. */
    public Boolean signRequests;
    public String completionWebhookUrl;

    public SetPrivacyOrchestratorBody() {
    }

    public SetPrivacyOrchestratorBody provider(String v) { this.provider = v; return this; }
    public SetPrivacyOrchestratorBody name(String v) { this.name = v; return this; }
    public SetPrivacyOrchestratorBody enabled(Boolean v) { this.enabled = v; return this; }
    public SetPrivacyOrchestratorBody caseUrl(String v) { this.caseUrl = v; return this; }
    public SetPrivacyOrchestratorBody cancelUrl(String v) { this.cancelUrl = v; return this; }
    public SetPrivacyOrchestratorBody authHeader(String v) { this.authHeader = v; return this; }
    public SetPrivacyOrchestratorBody authScheme(String v) { this.authScheme = v; return this; }
    public SetPrivacyOrchestratorBody apiKey(String v) { this.apiKey = v; return this; }
    public SetPrivacyOrchestratorBody usePartnerLink(Boolean v) { this.usePartnerLink = v; return this; }
    public SetPrivacyOrchestratorBody partnerLinkId(String v) { this.partnerLinkId = v; return this; }
    public SetPrivacyOrchestratorBody siteId(String v) { this.siteId = v; return this; }
    public SetPrivacyOrchestratorBody cancelDeletionOnSignIn(Boolean v) { this.cancelDeletionOnSignIn = v; return this; }
    public SetPrivacyOrchestratorBody openClosureCases(Boolean v) { this.openClosureCases = v; return this; }
    public SetPrivacyOrchestratorBody signRequests(Boolean v) { this.signRequests = v; return this; }
    public SetPrivacyOrchestratorBody completionWebhookUrl(String v) { this.completionWebhookUrl = v; return this; }

    /** Send the named nullable fields (wire names) as {@code null}, clearing the stored value. */
    public SetPrivacyOrchestratorBody clear(String... keys) { putNulls(keys); return this; }

    /** Set any field by its wire name, including one this SDK does not model yet. */
    public SetPrivacyOrchestratorBody set(String key, Object value) { putField(key, value); return this; }

}
