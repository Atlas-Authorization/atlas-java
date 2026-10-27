package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code POST /v1/proxy_apps}. Required: name, hostname, upstreamUrl. */
public final class ProxyAppsCreateBody {
    /** Required. */
    public String name;
    /** Required. */
    public String hostname;
    /** Required. */
    public String upstreamUrl;
    /** Path / method rules requiring a role, permission or entitlement. */
    public List<Map<String, Object>> rules;
    public List<String> publicPaths;
    public Boolean injectHeaders;
    public Boolean injectToken;
    /** Also let an active agent through with its machine token (for example to front an MCP gateway). Its scopes count as the permissions rules check. Default false. */
    public Boolean allowAgentTokens;
    public Boolean enabled;

    public ProxyAppsCreateBody() {
    }

    public ProxyAppsCreateBody name(String v) { this.name = v; return this; }
    public ProxyAppsCreateBody hostname(String v) { this.hostname = v; return this; }
    public ProxyAppsCreateBody upstreamUrl(String v) { this.upstreamUrl = v; return this; }
    public ProxyAppsCreateBody rules(List<Map<String, Object>> v) { this.rules = v; return this; }
    public ProxyAppsCreateBody publicPaths(List<String> v) { this.publicPaths = v; return this; }
    public ProxyAppsCreateBody injectHeaders(Boolean v) { this.injectHeaders = v; return this; }
    public ProxyAppsCreateBody injectToken(Boolean v) { this.injectToken = v; return this; }
    public ProxyAppsCreateBody allowAgentTokens(Boolean v) { this.allowAgentTokens = v; return this; }
    public ProxyAppsCreateBody enabled(Boolean v) { this.enabled = v; return this; }
}
