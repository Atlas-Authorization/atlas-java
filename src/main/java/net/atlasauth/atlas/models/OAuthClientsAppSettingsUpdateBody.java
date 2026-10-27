package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code PATCH /v1/oauth_clients/{id}/app_settings}. */
public final class OAuthClientsAppSettingsUpdateBody {
    /** The sign-in methods a session must have used for this app: password, email_code, email_link, passkey, phone_code, siwe, siws, siwf, a provider such as oauth_google, or the families oauth (any social provider) and enterprise_sso (any enterprise connection). The hosted pages offer only these when the */
    public List<String> allowedSignInMethods;
    /** The heading, sub-heading, logo and primary colour of the hosted sign-in and sign-up pages when the sign-in is for this app. */
    public Map<String, Object> branding;
    /** The user claims this app receives in its ID token and from userinfo (for example email, name, org_id). Protocol claims (iss, sub, aud, exp, iat, nonce, auth_time, acr, amr, azp, sid) always pass. [] = protocol claims only. */
    public List<String> claimsAllowlist;
    /** The identity traits a session that came from this app (a native app that exchanged its token for a session) may change through PATCH /v1/client/me. Others are refused with 403. */
    public List<String> writableTraits;
    /** Overrides of the instance's riskBasedMfa for sign-ins to this app (a sign-in started from this app's /oauth2/authorize request). A field left null keeps the instance value; weights, signals and reputation feeds stay instance-wide. */
    public Map<String, Object> risk;

    public OAuthClientsAppSettingsUpdateBody() {
    }

    public OAuthClientsAppSettingsUpdateBody allowedSignInMethods(List<String> v) { this.allowedSignInMethods = v; return this; }
    public OAuthClientsAppSettingsUpdateBody branding(Map<String, Object> v) { this.branding = v; return this; }
    public OAuthClientsAppSettingsUpdateBody claimsAllowlist(List<String> v) { this.claimsAllowlist = v; return this; }
    public OAuthClientsAppSettingsUpdateBody writableTraits(List<String> v) { this.writableTraits = v; return this; }
    public OAuthClientsAppSettingsUpdateBody risk(Map<String, Object> v) { this.risk = v; return this; }
}
