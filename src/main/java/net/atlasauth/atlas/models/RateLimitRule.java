package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * One per-endpoint rate-limit rule: at most {@code max} requests per {@code windowSeconds}
 * for the named {@code group}, counted per {@code key} ({@code ip}, {@code user} or
 * {@code instance}). A rule on a group that owns a built-in budget replaces it; any
 * other rule adds a budget on top.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public final class RateLimitRule {
    public String group;
    public Integer windowSeconds;
    public Integer max;
    public String key;

    public RateLimitRule() {
    }

    public RateLimitRule(String group, int windowSeconds, int max, String key) {
        this.group = group;
        this.windowSeconds = windowSeconds;
        this.max = max;
        this.key = key;
    }
}
