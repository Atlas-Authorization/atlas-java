package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Map;

/**
 * One append-only row of the anti-bot signal lake: a fully anonymised feature
 * vector per auth interaction. IPs are salt-hashed at ingest and behaviour is
 * timing aggregates only — nothing here is a raw secret.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record BotSignal(
        String object,
        String id,
        String attemptId,
        String userId,
        String kind,
        String source,
        String ipHash,
        String ipSubnet,
        Long asn,
        String geoCountry,
        String geoCity,
        Boolean isDatacenter,
        Boolean isAnonymizer,
        String headerFingerprint,
        String acceptLanguage,
        String deviceId,
        String deviceFingerprint,
        Map<String, Object> deviceFeatures,
        Map<String, Object> behaviorFeatures,
        String captchaProvider,
        Double captchaScore,
        String riskLevel,
        Double riskScore,
        Map<String, Object> riskSignals,
        Double heuristicScore,
        List<Object> heuristicReasons,
        String outcome,
        long createdAt) {
}
