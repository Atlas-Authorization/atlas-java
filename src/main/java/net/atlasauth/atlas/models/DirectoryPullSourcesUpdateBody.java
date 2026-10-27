package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code PATCH /v1/directory_pull_sources/{id}}. */
public final class DirectoryPullSourcesUpdateBody {
    public String organizationId;
    public String name;
    public String provider;
    public String status;
    public Map<String, Object> config;
    /** Write-only. */
    public Map<String, Object> credentials;
    public String defaultRoleId;
    /** Schedule, role mappings and disable-on-removal. */
    public Map<String, Object> settings;

    public DirectoryPullSourcesUpdateBody() {
    }

    public DirectoryPullSourcesUpdateBody organizationId(String v) { this.organizationId = v; return this; }
    public DirectoryPullSourcesUpdateBody name(String v) { this.name = v; return this; }
    public DirectoryPullSourcesUpdateBody provider(String v) { this.provider = v; return this; }
    public DirectoryPullSourcesUpdateBody status(String v) { this.status = v; return this; }
    public DirectoryPullSourcesUpdateBody config(Map<String, Object> v) { this.config = v; return this; }
    public DirectoryPullSourcesUpdateBody credentials(Map<String, Object> v) { this.credentials = v; return this; }
    public DirectoryPullSourcesUpdateBody defaultRoleId(String v) { this.defaultRoleId = v; return this; }
    public DirectoryPullSourcesUpdateBody settings(Map<String, Object> v) { this.settings = v; return this; }
}
