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
        /**
         * DNS automation, when set: {@code provider}, {@code token_set} (the token is never returned), {@code status}
         * (pending, verifying, verified or failed), {@code last_error}, {@code attempts}, {@code next_check_at} and
         * {@code started_at}. A create with {@code dns} answers 202 at once and Atlas writes the records in the background.
         */ Map<String, Object> dns,
        String failureReason,
        Long lastCheckedAt,
        Long verifiedAt,
        long createdAt,
        long updatedAt,
        /** No longer returned (DNS is written in the background; see {@code dns}). Always null from current servers. */ Map<String, Object> dnsResult,
        String nextStep,
        /** The email service the domain lives on (your own account there): resend or brevo. */ String backend,
        /** Your email provider (mode managed_domain) that holds the API key. */ String providerId,
        /** Whether the bounce / complaint webhook is registered on your provider account: registered or not_registered. */ String feedbackWebhook,
        /** That webhook's id on your account. */ String feedbackWebhookId,
        /** Why the last registration failed; null once it is registered. */ String feedbackWebhookError) {
    /** The 1.18 shape, without {@code feedbackWebhookId} and {@code feedbackWebhookError}. */
    public ManagedEmail(String object, String id, String fromAddress, String domain, String provider, String region, String status, boolean verified, List<ManagedEmailRecord> records, Map<String, Integer> limits, Map<String, Object> usage, String reputation, Map<String, Object> dns, String failureReason, Long lastCheckedAt, Long verifiedAt, long createdAt, long updatedAt, Map<String, Object> dnsResult, String nextStep, String backend, String providerId, String feedbackWebhook) {
        this(object, id, fromAddress, domain, provider, region, status, verified, records, limits, usage, reputation, dns, failureReason, lastCheckedAt, verifiedAt, createdAt, updatedAt, dnsResult, nextStep, backend, providerId, feedbackWebhook, null, null);
    }

    /** The 1.4 shape, without {@code backend}, {@code providerId}, {@code feedbackWebhook}. */
    public ManagedEmail(String object, String id, String fromAddress, String domain, String provider, String region, String status, boolean verified, List<ManagedEmailRecord> records, Map<String, Integer> limits, Map<String, Object> usage, String reputation, Map<String, Object> dns, String failureReason, Long lastCheckedAt, Long verifiedAt, long createdAt, long updatedAt, Map<String, Object> dnsResult, String nextStep) {
        this(object, id, fromAddress, domain, provider, region, status, verified, records, limits, usage, reputation, dns, failureReason, lastCheckedAt, verifiedAt, createdAt, updatedAt, dnsResult, nextStep, null, null, null, null, null);
    }
}
