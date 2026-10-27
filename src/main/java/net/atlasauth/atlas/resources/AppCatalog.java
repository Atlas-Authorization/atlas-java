package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.AppCatalogInstallBody;
import net.atlasauth.atlas.models.AppCatalogInstalledResponse;
import net.atlasauth.atlas.models.AppCatalogInstalledResponseDataItem;
import net.atlasauth.atlas.models.AppCatalogPresetsResponse;
import net.atlasauth.atlas.models.AppCatalogUpdateBody;

import java.util.List;
import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** The {@code /v1/app_catalog} resource. */
public final class AppCatalog {

    private final Transport t;

    public AppCatalog(Transport t) {
        this.t = t;
    }

    /** {@code GET /v1/app_catalog} - List the app presets: about 100 SAML apps (Slack, GitHub, Snowflake, Notion, HubSpot and more) and about 45 OpenID Connect apps (Grafana, GitLab, Argo CD, Vault, Cloudflare Zero Trust and more), each with its protocol, category, the parameters it needs, whether its values were confirmed in the vendor's docs (verified) and that page (source_url). Scope: {@code app_catalog:read}. */
    public AppCatalogPresetsResponse presets() {
        return presets(null, null);
    }

    /** As above, with every optional parameter. */
    public AppCatalogPresetsResponse presets(Long limit, String startingAfter) {
        Map<String, Object> q = Params.of().set("limit", limit).set("starting_after", startingAfter).map();
        return t.request("GET", "/v1/app_catalog", q, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/app_catalog/installed} - List the installed apps (SAML and OpenID Connect) with their email-domain allow-list and assignment. Scope: {@code app_catalog:read}. */
    public AppCatalogInstalledResponse installed() {
        return installed(null, null);
    }

    /** As above, with every optional parameter. */
    public AppCatalogInstalledResponse installed(Long limit, String startingAfter) {
        Map<String, Object> q = Params.of().set("limit", limit).set("starting_after", startingAfter).map();
        return t.request("GET", "/v1/app_catalog/installed", q, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/app_catalog/{id}/install} - Install a catalog app from its preset and params, with an optional name, installed_id, allowed_email_domains and assignment. A SAML preset becomes a SAML service provider; an OIDC preset registers a confidential OAuth client with the preset's redirect URIs and returns client_id, client_secret (shown once), redirect_uri and issuer to paste into the app. 409 if the installed_id exists. -&gt; 201. Scope: {@code app_catalog:write}. */
    public AppCatalogInstalledResponseDataItem install(String id, AppCatalogInstallBody body) {
        return install(id, body, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public AppCatalogInstalledResponseDataItem install(String id, AppCatalogInstallBody body, String idempotencyKey) {
        return t.request("POST", "/v1/app_catalog/" + enc(id) + "/install", null, body, idempotencyKey, false, new TypeReference<>() {
        });
    }

    /** {@code PATCH /v1/app_catalog/installed/{id}} - Change an installed app's name, allowed_email_domains or assignment. Assignment applies to the launcher and to single sign-on: someone not assigned is refused with 403. Scope: {@code app_catalog:write}. */
    public AppCatalogInstalledResponseDataItem update(String id, AppCatalogUpdateBody body) {
        return t.request("PATCH", "/v1/app_catalog/installed/" + enc(id), null, body, null, false, new TypeReference<>() {
        });
    }

    /** {@code DELETE /v1/app_catalog/installed/{id}} - Uninstall an app: it disappears from the launcher and Atlas stops signing people in to it (an OIDC app's OAuth client and its grants are deleted). Scope: {@code app_catalog:write}. */
    public JsonNode uninstall(String id) {
        return t.request("DELETE", "/v1/app_catalog/installed/" + enc(id), null, null, null, false, new TypeReference<JsonNode>() {
        });
    }
}
