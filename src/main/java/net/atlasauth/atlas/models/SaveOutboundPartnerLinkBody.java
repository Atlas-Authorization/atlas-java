package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code PUT /v1/partner_links/outbound/{partner}}: a credential this instance holds for a partner API. */
public final class SaveOutboundPartnerLinkBody extends ExtensibleBody {
    public String clientId;
    /** Write-only. */
    public String clientSecret;
    public List<String> scopes;
    public String resource;
    /** Required for a partner that is not a preset. */
    public String baseUrl;
    /** Required for a partner that is not a preset. */
    public String tokenUrl;
    /** {@code client_credentials} or {@code bearer_secret}. */
    public String authMethod;
    public String name;
    /** Unix ms. */
    public Long expiresAt;

    public SaveOutboundPartnerLinkBody() {
    }

    public SaveOutboundPartnerLinkBody clientId(String v) { this.clientId = v; return this; }
    public SaveOutboundPartnerLinkBody clientSecret(String v) { this.clientSecret = v; return this; }
    public SaveOutboundPartnerLinkBody scopes(List<String> v) { this.scopes = v; return this; }
    public SaveOutboundPartnerLinkBody resource(String v) { this.resource = v; return this; }
    public SaveOutboundPartnerLinkBody baseUrl(String v) { this.baseUrl = v; return this; }
    public SaveOutboundPartnerLinkBody tokenUrl(String v) { this.tokenUrl = v; return this; }
    public SaveOutboundPartnerLinkBody authMethod(String v) { this.authMethod = v; return this; }
    public SaveOutboundPartnerLinkBody name(String v) { this.name = v; return this; }
    public SaveOutboundPartnerLinkBody expiresAt(Long v) { this.expiresAt = v; return this; }

    /** Set any field by its wire name, including one this SDK does not model yet. */
    public SaveOutboundPartnerLinkBody set(String key, Object value) { putField(key, value); return this; }

}
