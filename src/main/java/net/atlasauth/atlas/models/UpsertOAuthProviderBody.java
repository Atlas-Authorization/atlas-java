package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Body for {@code PUT /v1/oauth_providers/:provider}. {@code clientId}/{@code
 * clientSecret} cover the common two-field case; {@code values} carries the exact
 * per-field shape for providers that need more (Apple team/key id, etc.), and
 * {@code settings} the provider-specific options. The secret is write-only.
 *
 * <p>Display-only fields ({@code displayName}, {@code iconUrl}, {@code mode}) can be
 * sent on their own, with no credentials, to re-brand a provider's button. The
 * credential-less wallet providers ({@code siwe}, {@code siws}, {@code siwf}) take
 * only their verifier {@code settings} plus display fields — see
 * {@link #siwe(String, String, String)}, {@link #siws(String, String)} and
 * {@link #siwf(String, String, String, String)}. A setting set to {@code null} (see
 * {@link #clearSetting(String)}) or {@code ""} clears it.
 */
public final class UpsertOAuthProviderBody {
    public String clientId;
    /** Write-only; stored encrypted, never returned. Required for redirect-only providers. */
    public String clientSecret;
    public List<String> scopes;
    public Map<String, String> values;
    public Map<String, String> settings;
    /** Override the redirect URI registered with the provider. */
    public String customRedirectUri;
    @JsonProperty("native")
    public Boolean native_;
    /** {@code "native"}, {@code "redirect"}, or {@code ""} to clear the override. */
    public String mode;
    /** The brand shown on the button (e.g. {@code "Discord"}); empty clears. */
    public String displayName;
    /** An http(s) or {@code data:image} icon URL for the button; empty clears. */
    public String iconUrl;

    public UpsertOAuthProviderBody() {
    }

    public UpsertOAuthProviderBody clientId(String v) { this.clientId = v; return this; }
    public UpsertOAuthProviderBody clientSecret(String v) { this.clientSecret = v; return this; }
    public UpsertOAuthProviderBody scopes(List<String> v) { this.scopes = v; return this; }
    public UpsertOAuthProviderBody values(Map<String, String> v) { this.values = v; return this; }
    public UpsertOAuthProviderBody settings(Map<String, String> v) { this.settings = v; return this; }
    public UpsertOAuthProviderBody customRedirectUri(String v) { this.customRedirectUri = v; return this; }
    public UpsertOAuthProviderBody nativeFlow(Boolean v) { this.native_ = v; return this; }
    public UpsertOAuthProviderBody mode(String v) { this.mode = v; return this; }
    public UpsertOAuthProviderBody displayName(String v) { this.displayName = v; return this; }
    public UpsertOAuthProviderBody iconUrl(String v) { this.iconUrl = v; return this; }

    /** Set one provider setting (a {@code null} value clears it). */
    public UpsertOAuthProviderBody setting(String key, String value) {
        if (this.settings == null) {
            this.settings = new LinkedHashMap<>();
        }
        this.settings.put(key, value);
        return this;
    }

    /** Clear one stored provider setting (sends it as {@code null}). */
    public UpsertOAuthProviderBody clearSetting(String key) {
        return setting(key, null);
    }

    /**
     * Sign in with Ethereum verifier settings. Any argument may be {@code null} to leave
     * it unchanged.
     *
     * @param domain   the expected hostname[:port] in the signed message
     * @param chainIds allowed EIP-155 chain ids, comma-separated (e.g. {@code "1,10"})
     * @param rpcUrl   an https RPC URL (enables EIP-1271 contract wallets)
     */
    public static UpsertOAuthProviderBody siwe(String domain, String chainIds, String rpcUrl) {
        UpsertOAuthProviderBody b = new UpsertOAuthProviderBody();
        putIfSet(b, "domain", domain);
        putIfSet(b, "chain_ids", chainIds);
        putIfSet(b, "rpc_url", rpcUrl);
        return b;
    }

    /**
     * Sign in with Solana verifier settings.
     *
     * @param domain   the expected hostname[:port] in the signed message
     * @param networks allowed networks, comma-separated ({@code mainnet}, {@code devnet}, {@code testnet})
     */
    public static UpsertOAuthProviderBody siws(String domain, String networks) {
        UpsertOAuthProviderBody b = new UpsertOAuthProviderBody();
        putIfSet(b, "domain", domain);
        putIfSet(b, "networks", networks);
        return b;
    }

    /**
     * Sign in with Farcaster verifier settings.
     *
     * @param domain            the expected hostname[:port] in the signed message
     * @param chainIds          allowed chain ids, comma-separated
     * @param optimismRpcUrl    an https Optimism RPC URL (required to verify FID ownership)
     * @param idRegistryAddress the Farcaster ID registry contract address ({@code 0x...})
     */
    public static UpsertOAuthProviderBody siwf(String domain, String chainIds, String optimismRpcUrl,
                                               String idRegistryAddress) {
        UpsertOAuthProviderBody b = new UpsertOAuthProviderBody();
        putIfSet(b, "domain", domain);
        putIfSet(b, "chain_ids", chainIds);
        putIfSet(b, "optimism_rpc_url", optimismRpcUrl);
        putIfSet(b, "id_registry_address", idRegistryAddress);
        return b;
    }

    private static void putIfSet(UpsertOAuthProviderBody b, String key, String value) {
        if (value != null) {
            b.setting(key, value);
        }
    }
}
