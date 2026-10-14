package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** Proof-of-possession confirmation — present only for a PoP key. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ApiKeyTokenCnf(
        String jkt) {
}
