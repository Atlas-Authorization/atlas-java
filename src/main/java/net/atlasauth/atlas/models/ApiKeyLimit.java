package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * A per-key spend cap, metered with {@code POST /v1/api_keys/{id}/usage}. Used both
 * on {@link CreateApiKeyBody#limits} / {@link UpdateApiKeyBody#limits} and on the
 * key a read returns.
 *
 * @param unit   app-defined unit, e.g. {@code "requests"} or {@code "usd_cents"}
 *               (1-64 letters, digits, {@code _ . : -})
 * @param amount the cap, a non-negative integer in {@code unit}
 * @param period {@code "request"} caps a single debit; {@code "second"} / {@code "minute"} are rolling
 *               rate windows; {@code "day"} / {@code "month"}
 *               are UTC calendar windows; {@code "total"} never resets
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ApiKeyLimit(String unit, long amount, String period) {

    public static final String PERIOD_REQUEST = "request";
    public static final String PERIOD_SECOND = "second";
    public static final String PERIOD_MINUTE = "minute";
    public static final String PERIOD_DAY = "day";
    public static final String PERIOD_MONTH = "month";
    public static final String PERIOD_TOTAL = "total";
}
