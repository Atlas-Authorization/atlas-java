package net.atlasauth.atlas.models;

/** A partial update of the referral security settings: only the fields set change. */
public final class UpdateReferralSecurityBody {
    /** always or inherit. */
    public String challengeReferredSignups;
    /** flag, strip or hold. */
    public String onHighRisk;
    public Boolean holdUntilEmailVerified;
    /** 1-1000. */
    public Integer maxReferredSignupsPerIpHour;

    public UpdateReferralSecurityBody() {
    }

    public UpdateReferralSecurityBody challengeReferredSignups(String v) { this.challengeReferredSignups = v; return this; }
    public UpdateReferralSecurityBody onHighRisk(String v) { this.onHighRisk = v; return this; }
    public UpdateReferralSecurityBody holdUntilEmailVerified(Boolean v) { this.holdUntilEmailVerified = v; return this; }
    public UpdateReferralSecurityBody maxReferredSignupsPerIpHour(Integer v) { this.maxReferredSignupsPerIpHour = v; return this; }
}
