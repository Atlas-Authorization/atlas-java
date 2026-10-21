package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Map;

/** Managed email: Atlas sets your sending domain up on your own Resend or Brevo account. Email sends only once {@code verified}. */
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
        /** Null: no Atlas sending limits apply (servers before 2026-10 sent {@code hourly} and {@code daily}). */ Map<String, Integer> limits,
        /** sent, bounces, complaints, last_bounce_at, last_complaint_at. */ Map<String, Object> usage,
        String reputation,
        /** {@code provider} and {@code token_set}; the token is never returned. */ Map<String, Object> dns,
        String failureReason,
        Long lastCheckedAt,
        Long verifiedAt,
        long createdAt,
        long updatedAt,
        /** Create only, with dns: the zone and the records Atlas wrote. */ Map<String, Object> dnsResult,
        String nextStep,
        /** The email service the domain lives on (your own account there): resend or brevo. */ String backend,
        /** Your email provider (mode managed_domain) that holds the API key. */ String providerId,
        /** Brevo, on create: registered or not_registered. */ String feedbackWebhook) {
    /** The 1.4 shape, without {@code backend}, {@code providerId}, {@code feedbackWebhook}. */
    public ManagedEmail(String object, String id, String fromAddress, String domain, String provider, String region, String status, boolean verified, List<ManagedEmailRecord> records, Map<String, Integer> limits, Map<String, Object> usage, String reputation, Map<String, Object> dns, String failureReason, Long lastCheckedAt, Long verifiedAt, long createdAt, long updatedAt, Map<String, Object> dnsResult, String nextStep) {
        this(object, id, fromAddress, domain, provider, region, status, verified, records, limits, usage, reputation, dns, failureReason, lastCheckedAt, verifiedAt, createdAt, updatedAt, dnsResult, nextStep, null, null, null);
    }
}
