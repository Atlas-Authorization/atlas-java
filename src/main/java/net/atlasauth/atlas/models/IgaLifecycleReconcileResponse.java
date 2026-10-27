package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code POST /v1/iga/users/{id}/reconcile}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record IgaLifecycleReconcileResponse(
        String object,
        String userId,
        List<String> granted,
        List<String> revoked) {
}
