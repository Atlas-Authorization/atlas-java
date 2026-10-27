package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code POST /v1/service_connectors}. */
public final class ServiceConnectorsCreateBody {
    public String name;
    public String kind;
    public Map<String, Object> config;
    public Map<String, Object> credentials;

    public ServiceConnectorsCreateBody() {
    }

    public ServiceConnectorsCreateBody name(String v) { this.name = v; return this; }
    public ServiceConnectorsCreateBody kind(String v) { this.kind = v; return this; }
    public ServiceConnectorsCreateBody config(Map<String, Object> v) { this.config = v; return this; }
    public ServiceConnectorsCreateBody credentials(Map<String, Object> v) { this.credentials = v; return this; }
}
