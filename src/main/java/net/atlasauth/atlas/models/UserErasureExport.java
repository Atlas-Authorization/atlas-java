package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record UserErasureExport(
        String object,
        long formatVersion,
        long generatedAt,
        String userId,
        Map<String, Object> atlas,
        List<UserErasureExportAppContributions> appContributions) {
}
