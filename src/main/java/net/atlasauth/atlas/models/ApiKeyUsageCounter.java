package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * A raw usage counter for one unit in its current window.
 *
 * @param period      {@code "day"}, {@code "month"} or {@code "total"}
 * @param windowStart epoch ms the window started
 * @param resetsAt    epoch ms the window ends; null for {@code total}
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ApiKeyUsageCounter(
        String unit,
        String period,
        long used,
        long windowStart,
        Long resetsAt) {
}
