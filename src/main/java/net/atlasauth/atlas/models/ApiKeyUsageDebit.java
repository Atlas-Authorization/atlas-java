package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * The outcome of {@code POST /v1/api_keys/{id}/usage} ({@code object: "api_key_usage_debit"}).
 * A debit that would exceed a cap is NOT an error: it answers {@code allowed=false}
 * with {@code code="LIMIT_EXCEEDED"} and records nothing.
 *
 * @param allowed          true when the debit was recorded
 * @param code             {@code "LIMIT_EXCEEDED"} when refused; null when allowed
 * @param exceeded         the caps that refused the debit (empty when allowed)
 * @param usage            every cap on the key after this call
 * @param counters         the debited unit's day, month and total counters after this call
 * @param idempotentReplay true when this is the stored outcome of an earlier call with
 *                         the same idempotency key (nothing was counted again)
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ApiKeyUsageDebit(
        String object,
        String keyId,
        String unit,
        long amount,
        boolean allowed,
        String code,
        List<ApiKeyLimitState> exceeded,
        List<ApiKeyLimitState> usage,
        List<ApiKeyUsageCounter> counters,
        boolean idempotentReplay) {
}
