package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/** {@code GET /v1/instance/connections}: the instance's links to other platforms. {@code ok} is false when a warning- or error-severity row fails. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record InstanceConnections(
        String object,
        boolean ok,
        long checkedAt,
        List<InstanceConnection> data) {
}
