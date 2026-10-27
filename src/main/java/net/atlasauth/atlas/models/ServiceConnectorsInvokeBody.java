package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code POST /v1/service_connectors/{id}/invoke}. */
public final class ServiceConnectorsInvokeBody {
    public String operation;
    public Map<String, Object> input;

    public ServiceConnectorsInvokeBody() {
    }

    public ServiceConnectorsInvokeBody operation(String v) { this.operation = v; return this; }
    public ServiceConnectorsInvokeBody input(Map<String, Object> v) { this.input = v; return this; }
}
