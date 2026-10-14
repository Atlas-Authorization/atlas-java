package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RequestInspection(
        String object,
        String origin,
        String method,
        String path,
        boolean wouldBeRejected,
        List<RequestInspectionFindings> findings) {
}
