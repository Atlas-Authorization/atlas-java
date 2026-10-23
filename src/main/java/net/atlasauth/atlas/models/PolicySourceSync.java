package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/** {@code POST /v1/instance/policy_source/sync}: fetch and store now. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record PolicySourceSync(
        String object,
        Boolean ok,
        String error,
        List<String> warnings,
        PolicySourceSyncChanges changes,
        PolicySourceDelivery delivery) {
}
