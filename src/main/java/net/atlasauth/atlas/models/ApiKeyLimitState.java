package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * A spend cap with its current-window usage.
 *
 * @param used        used in the current window (always 0 for a per-request cap)
 * @param remaining   {@code amount - used}, never below 0 (the cap itself for a per-request cap)
 * @param windowStart epoch ms the current window started (UTC); null for a per-request cap
 * @param resetsAt    epoch ms the current window ends (UTC); null for per-request and total caps
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ApiKeyLimitState(
        String unit,
        String period,
        long amount,
        long used,
        long remaining,
        Long windowStart,
        Long resetsAt) {
}
