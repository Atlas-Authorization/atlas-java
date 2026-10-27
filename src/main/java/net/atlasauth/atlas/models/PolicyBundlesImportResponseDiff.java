package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** A response object returned by the Backend API. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record PolicyBundlesImportResponseDiff(
        Map<String, Object> abacPolicies,
        Map<String, Object> fgaStores,
        Map<String, Object> abacTemplates,
        Map<String, Object> abacLinks,
        String abacSchema,
        Boolean identical) {
}
