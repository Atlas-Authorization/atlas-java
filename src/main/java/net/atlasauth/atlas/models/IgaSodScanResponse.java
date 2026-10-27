package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** The response of {@code POST /v1/iga/sod_violations/scan}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record IgaSodScanResponse(
        String object,
        Long found,
        Long resolved) {
}
