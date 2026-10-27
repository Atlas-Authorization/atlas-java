package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code PATCH /v1/proxy_apps/{id}}. */
public final class ProxyAppsUpdateBody {
    public String name;
    public String hostname;
    public String upstreamUrl;
    /** Path / method rules requiring a role, permission or entitlement. */
    public List<Map<String, Object>> rules;
    public List<String> publicPaths;
    public Boolean injectHeaders;
    public Boolean injectToken;
    /** Also let an active agent through with its machine token (for example to front an MCP gateway). Its scopes count as the permissions rules check. Default false. */
    public Boolean allowAgentTokens;
    public Boolean enabled;

    public ProxyAppsUpdateBody() {
    }

    public ProxyAppsUpdateBody name(String v) { this.name = v; return this; }
    public ProxyAppsUpdateBody hostname(String v) { this.hostname = v; return this; }
    public ProxyAppsUpdateBody upstreamUrl(String v) { this.upstreamUrl = v; return this; }
    public ProxyAppsUpdateBody rules(List<Map<String, Object>> v) { this.rules = v; return this; }
    public ProxyAppsUpdateBody publicPaths(List<String> v) { this.publicPaths = v; return this; }
    public ProxyAppsUpdateBody injectHeaders(Boolean v) { this.injectHeaders = v; return this; }
    public ProxyAppsUpdateBody injectToken(Boolean v) { this.injectToken = v; return this; }
    public ProxyAppsUpdateBody allowAgentTokens(Boolean v) { this.allowAgentTokens = v; return this; }
    public ProxyAppsUpdateBody enabled(Boolean v) { this.enabled = v; return this; }
}
