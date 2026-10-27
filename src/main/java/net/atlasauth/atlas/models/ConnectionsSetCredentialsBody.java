package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code PUT /v1/connections/{id}/credentials}. */
public final class ConnectionsSetCredentialsBody {
    /** api_key. */
    public String apiKey;
    /** bearer; or the token of a tba/three-legged oauth1 credential. */
    public String token;
    /** basic (no colon). */
    public String username;
    /** basic. */
    public String password;
    /** custom_header. */
    public String value;
    /** hmac_secret. */
    public String secret;
    /** client_credentials, or the app client id of a jwt_bearer with Basic client authentication. */
    public String clientId;
    /** client_credentials / jwt_bearer client authentication. */
    public String clientSecret;
    /** https token endpoint (client_credentials, jwt_bearer); `&#123;name&#125;` placeholders are filled from host_params. */
    public String tokenUrl;
    public List<String> scopes;
    /** jwt_bearer / github_app_installation: a PEM private key (RSA or P-256). Held in the vault; never returned. */
    public String privateKey;
    /** jwt_bearer: have Atlas generate the key pair; the public JWK is returned in config.public_jwk for you to register at the provider. */
    public Boolean generateKey;
    /** jwt_bearer: a Google service-account JSON key (a string or the parsed object). */
    public Object serviceAccountJson;
    /** jwt_bearer with a provider-issued JWT credential (RingCentral). */
    public String assertion;
    /** oauth1. */
    public String consumerKey;
    /** oauth1. */
    public String consumerSecret;
    /** oauth1. */
    public String tokenSecret;
    /** Non-secret settings to merge (same keys as the connection config). */
    public Map<String, Object> config;
    /** When this credential expires: epoch milliseconds or an ISO date. Reminders are sent at 30, 7 and 1 days before. */
    public Object expiresAt;

    public ConnectionsSetCredentialsBody() {
    }

    public ConnectionsSetCredentialsBody apiKey(String v) { this.apiKey = v; return this; }
    public ConnectionsSetCredentialsBody token(String v) { this.token = v; return this; }
    public ConnectionsSetCredentialsBody username(String v) { this.username = v; return this; }
    public ConnectionsSetCredentialsBody password(String v) { this.password = v; return this; }
    public ConnectionsSetCredentialsBody value(String v) { this.value = v; return this; }
    public ConnectionsSetCredentialsBody secret(String v) { this.secret = v; return this; }
    public ConnectionsSetCredentialsBody clientId(String v) { this.clientId = v; return this; }
    public ConnectionsSetCredentialsBody clientSecret(String v) { this.clientSecret = v; return this; }
    public ConnectionsSetCredentialsBody tokenUrl(String v) { this.tokenUrl = v; return this; }
    public ConnectionsSetCredentialsBody scopes(List<String> v) { this.scopes = v; return this; }
    public ConnectionsSetCredentialsBody privateKey(String v) { this.privateKey = v; return this; }
    public ConnectionsSetCredentialsBody generateKey(Boolean v) { this.generateKey = v; return this; }
    public ConnectionsSetCredentialsBody serviceAccountJson(Object v) { this.serviceAccountJson = v; return this; }
    public ConnectionsSetCredentialsBody assertion(String v) { this.assertion = v; return this; }
    public ConnectionsSetCredentialsBody consumerKey(String v) { this.consumerKey = v; return this; }
    public ConnectionsSetCredentialsBody consumerSecret(String v) { this.consumerSecret = v; return this; }
    public ConnectionsSetCredentialsBody tokenSecret(String v) { this.tokenSecret = v; return this; }
    public ConnectionsSetCredentialsBody config(Map<String, Object> v) { this.config = v; return this; }
    public ConnectionsSetCredentialsBody expiresAt(Object v) { this.expiresAt = v; return this; }
}
