package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code POST /v1/directory_pull_sources}. Required: organizationId, name, provider. */
public final class DirectoryPullSourcesCreateBody {
    /** Required. */
    public String organizationId;
    /** Required. */
    public String name;
    /** Required. */
    public String provider;
    public String status;
    public Map<String, Object> config;
    /** Write-only. */
    public Map<String, Object> credentials;
    public String defaultRoleId;
    /** Schedule, role mappings and disable-on-removal. */
    public Map<String, Object> settings;

    public DirectoryPullSourcesCreateBody() {
    }

    public DirectoryPullSourcesCreateBody organizationId(String v) { this.organizationId = v; return this; }
    public DirectoryPullSourcesCreateBody name(String v) { this.name = v; return this; }
    public DirectoryPullSourcesCreateBody provider(String v) { this.provider = v; return this; }
    public DirectoryPullSourcesCreateBody status(String v) { this.status = v; return this; }
    public DirectoryPullSourcesCreateBody config(Map<String, Object> v) { this.config = v; return this; }
    public DirectoryPullSourcesCreateBody credentials(Map<String, Object> v) { this.credentials = v; return this; }
    public DirectoryPullSourcesCreateBody defaultRoleId(String v) { this.defaultRoleId = v; return this; }
    public DirectoryPullSourcesCreateBody settings(Map<String, Object> v) { this.settings = v; return this; }
}
