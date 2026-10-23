package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Dormant-account reactivation: when an account idle for {@code thresholdDays} (1–3650,
 * default 90) signs in again there is an audit entry, the {@code user.reactivated}
 * webhook, an optional notice to the user and, with {@code requireMfa}, a second-factor
 * step-up ({@code onNoFactor}: {@code allow}, {@code require_enroll} or {@code block}).
 * On an update, unset fields are left unchanged.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public final class DormantAccountsSettings {
    public Boolean enabled;
    public Integer thresholdDays;
    public Boolean requireMfa;
    public String onNoFactor;
    public Boolean notifyUser;

    public DormantAccountsSettings() {
    }

    public DormantAccountsSettings enabled(Boolean v) { this.enabled = v; return this; }
    public DormantAccountsSettings thresholdDays(Integer v) { this.thresholdDays = v; return this; }
    public DormantAccountsSettings requireMfa(Boolean v) { this.requireMfa = v; return this; }
    public DormantAccountsSettings onNoFactor(String v) { this.onNoFactor = v; return this; }
    public DormantAccountsSettings notifyUser(Boolean v) { this.notifyUser = v; return this; }
}
