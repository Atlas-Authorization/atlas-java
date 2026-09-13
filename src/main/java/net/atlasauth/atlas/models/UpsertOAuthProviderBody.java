package net.atlasauth.atlas.models;

import java.util.List;
import java.util.Map;

/**
 * Body for {@code PUT /v1/oauth_providers/:provider}. {@code clientId}/{@code
 * clientSecret} cover the common two-field case; {@code values} carries the exact
 * per-field shape for providers that need more (Apple team/key id, etc.), and
 * {@code settings} the provider-specific options. The secret is write-only.
 */
public final class UpsertOAuthProviderBody {
    public String clientId;
    /** Write-only; stored encrypted, never returned. Required for redirect-only providers. */
    public String clientSecret;
    public List<String> scopes;
    public Map<String, String> values;
    public Map<String, String> settings;

    public UpsertOAuthProviderBody() {
    }

    public UpsertOAuthProviderBody clientId(String v) { this.clientId = v; return this; }
    public UpsertOAuthProviderBody clientSecret(String v) { this.clientSecret = v; return this; }
    public UpsertOAuthProviderBody scopes(List<String> v) { this.scopes = v; return this; }
    public UpsertOAuthProviderBody values(Map<String, String> v) { this.values = v; return this; }
    public UpsertOAuthProviderBody settings(Map<String, String> v) { this.settings = v; return this; }
}
