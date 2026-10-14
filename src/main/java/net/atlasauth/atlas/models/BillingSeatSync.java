package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record BillingSeatSync(
        String object,
        String subjectType,
        String subjectId,
        long quantity) {
}
