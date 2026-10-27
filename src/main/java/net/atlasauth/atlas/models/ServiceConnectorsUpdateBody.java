package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code PATCH /v1/service_connectors/{id}}. */
public final class ServiceConnectorsUpdateBody {
    public String name;
    public Boolean enabled;
    public Map<String, Object> config;
    public Map<String, Object> credentials;

    public ServiceConnectorsUpdateBody() {
    }

    public ServiceConnectorsUpdateBody name(String v) { this.name = v; return this; }
    public ServiceConnectorsUpdateBody enabled(Boolean v) { this.enabled = v; return this; }
    public ServiceConnectorsUpdateBody config(Map<String, Object> v) { this.config = v; return this; }
    public ServiceConnectorsUpdateBody credentials(Map<String, Object> v) { this.credentials = v; return this; }
}
