package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Map;

/** Atlas-managed email: your own sending domain on Atlas's email account. Email sends only once {@code verified}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ManagedEmail(
        String object,
        String id,
        String fromAddress,
        String domain,
        String provider,
        String region,
        /** not_started, pending, verified, failed or temporary_failure. */ String status,
        boolean verified,
        List<ManagedEmailRecord> records,
        /** {@code hourly} and {@code daily}. */ Map<String, Integer> limits,
        /** sent, bounces, complaints, last_bounce_at, last_complaint_at. */ Map<String, Object> usage,
        String reputation,
        /** {@code provider} and {@code token_set}; the token is never returned. */ Map<String, Object> dns,
        String failureReason,
        Long lastCheckedAt,
        Long verifiedAt,
        long createdAt,
        long updatedAt,
        /** Create only, with dns: the zone and the records Atlas wrote. */ Map<String, Object> dnsResult,
        String nextStep) {
}
