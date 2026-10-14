package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PcaStepUp(
        String object,
        String stepupId,
        String grantRef,
        String actionDigest,
        Long requiredT,
        String status,
        List<String> shares,
        String createdAt,
        String expiresAt,
        Map<String, Object> receipt,
        Boolean stepUpRequired) {
}
