package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** {@code DELETE /v1/policies/:id}: a never-published policy is deleted, one with history is retired. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record DeletedPolicy(
        String object,
        String id,
        String key,
        Boolean deleted,
        Boolean retired,
        Policy policy) {
}
