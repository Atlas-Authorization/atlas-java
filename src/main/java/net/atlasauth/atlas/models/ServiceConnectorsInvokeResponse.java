package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code POST /v1/service_connectors/{id}/invoke}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ServiceConnectorsInvokeResponse(
        String object,
        String operation,
        Map<String, Object> result) {
}
