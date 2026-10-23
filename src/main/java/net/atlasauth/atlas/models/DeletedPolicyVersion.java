package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** A deleted draft version. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record DeletedPolicyVersion(
        String object,
        String id,
        Boolean deleted) {
}
