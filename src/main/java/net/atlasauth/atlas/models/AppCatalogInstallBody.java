package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code POST /v1/app_catalog/{id}/install}. */
public final class AppCatalogInstallBody {
    /** The preset's parameters (GET /v1/app_catalog lists them). */
    public Map<String, Object> params;
    /** 1-64 letters, digits, - or _. Defaults to the preset id. */
    public String installedId;
    public String name;
    public List<String> allowedEmailDomains;
    /** Who may open the app. mode everyone (default) or assigned; with assigned, only the listed users, members of the listed instance groups (id or slug, inherited), members of the listed organizations or holders of the listed organization roles. allowed_email_domains applies on top. */
    public Map<String, Object> assignment;

    public AppCatalogInstallBody() {
    }

    public AppCatalogInstallBody params(Map<String, Object> v) { this.params = v; return this; }
    public AppCatalogInstallBody installedId(String v) { this.installedId = v; return this; }
    public AppCatalogInstallBody name(String v) { this.name = v; return this; }
    public AppCatalogInstallBody allowedEmailDomains(List<String> v) { this.allowedEmailDomains = v; return this; }
    public AppCatalogInstallBody assignment(Map<String, Object> v) { this.assignment = v; return this; }
}
