package net.atlasauth.atlas.models;

/**
 * An imported referral on {@code POST /v1/users}: how the user arrived. With referral
 * attribution on, the user is reported to the referral platform. Each id is 3-64 characters
 * of {@code A-Z a-z 0-9 _ -}.
 */
public final class CreateUserReferral {
    public String code;
    public String clickId;
    public String anonId;

    public CreateUserReferral() {
    }

    public CreateUserReferral code(String v) { this.code = v; return this; }
    public CreateUserReferral clickId(String v) { this.clickId = v; return this; }
    public CreateUserReferral anonId(String v) { this.anonId = v; return this; }
}
