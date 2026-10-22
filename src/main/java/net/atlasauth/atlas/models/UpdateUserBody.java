package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.LinkedHashMap;
import java.util.Map;

/** Body for {@code PATCH /v1/users/:id}. Unset fields are omitted. */
public final class UpdateUserBody {
    public String firstName;
    public String lastName;
    public Map<String, Object> publicMetadata;
    public Map<String, Object> privateMetadata;
    /** The user's own referral code, unique per instance (409 {@code REFERRAL_CODE_TAKEN}). */
    public String referralCode;
    /**
     * Your own id for the user. With referral attribution on, a change after sign-up is forwarded
     * to the referral platform as an alias.
     */
    public String externalId;

    @JsonIgnore
    private final Map<String, Object> additionalFields = new LinkedHashMap<>();

    public UpdateUserBody firstName(String v) { this.firstName = v; return this; }
    public UpdateUserBody lastName(String v) { this.lastName = v; return this; }
    public UpdateUserBody publicMetadata(Map<String, Object> v) { this.publicMetadata = v; return this; }
    public UpdateUserBody privateMetadata(Map<String, Object> v) { this.privateMetadata = v; return this; }
    public UpdateUserBody referralCode(String v) { this.referralCode = v; this.additionalFields.remove("referral_code"); return this; }

    public UpdateUserBody externalId(String v) { this.externalId = v; this.additionalFields.remove("external_id"); return this; }

    /** Remove the user's external id ({@code external_id: null}). */
    public UpdateUserBody clearExternalId() { this.externalId = null; this.additionalFields.put("external_id", null); return this; }

    /** Remove the user's referral code ({@code referral_code: null}). */
    public UpdateUserBody clearReferralCode() { this.referralCode = null; this.additionalFields.put("referral_code", null); return this; }

    @JsonAnyGetter
    public Map<String, Object> additionalFields() { return additionalFields; }
}
