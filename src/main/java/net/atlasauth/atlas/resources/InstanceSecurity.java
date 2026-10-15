package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.InstanceSecurityConfig;
import net.atlasauth.atlas.models.InstanceSecurityResult;
import net.atlasauth.atlas.models.UpdateInstanceSecurityBody;

import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/**
 * The {@code /v1/instance/security} resource — kill switches, IP allowlist, and
 * the write-only provider secrets (captcha, Kerberos proxy, LDAP bind password).
 * No secret is ever readable back.
 */
public final class InstanceSecurity {

    private final Transport t;

    public InstanceSecurity(Transport t) {
        this.t = t;
    }

    public InstanceSecurityConfig get() {
        return t.get("/v1/instance/security", new TypeReference<>() {
        });
    }

    public InstanceSecurityResult update(UpdateInstanceSecurityBody body) {
        return t.patch("/v1/instance/security", body, new TypeReference<>() {
        });
    }

    /** Store the captcha secret for the configured provider. */
    public JsonNode setCaptchaSecret(String secret) {
        return setCaptchaSecret(secret, null);
    }

    /**
     * Store a captcha provider secret. {@code provider} (null: the configured one) lets you
     * store it BEFORE turning captcha on: captcha is bring-your-own-keys, and enabling it
     * without a site key and secret is refused with 422 {@code CAPTCHA_KEYS_REQUIRED}.
     */
    public JsonNode setCaptchaSecret(String secret, String provider) {
        Map<String, Object> body = Params.of("secret", secret).set("provider", provider).map();
        return t.put("/v1/instance/captcha_secret", body, new TypeReference<>() {
        });
    }

    /** Remove the configured provider's captcha secret. */
    public JsonNode deleteCaptchaSecret() {
        return deleteCaptchaSecret(null, false);
    }

    /**
     * Remove a captcha secret. Refused with 409 {@code CAPTCHA_IN_USE} while a flow uses it
     * ({@code meta.flows}); {@code force} removes it anyway and turns those flows off
     * (returned as {@code flows_turned_off}).
     */
    public JsonNode deleteCaptchaSecret(String provider, boolean force) {
        Map<String, Object> q = Params.of().set("provider", provider).set("force", force ? Boolean.TRUE : null).map();
        return t.delete("/v1/instance/captcha_secret", q, new TypeReference<>() {
        });
    }

    /** Store the Kerberos/IWA trusted-proxy secret. Refused while the strategy is off. */
    public JsonNode setKerberosSecret(String secret) {
        Map<String, Object> body = Params.of("secret", secret).map();
        return t.put("/v1/instance/kerberos_secret", body, new TypeReference<>() {
        });
    }

    public JsonNode deleteKerberosSecret() {
        return t.delete("/v1/instance/kerberos_secret", new TypeReference<>() {
        });
    }

    /**
     * Store an LDAP connection's service-account bind password. The connection
     * must already exist in {@code auth_config.ldap.connections}.
     */
    public JsonNode setLdapBindPassword(String connectionId, String bindPassword) {
        Map<String, Object> body = Params.of("bind_password", bindPassword).map();
        return t.put("/v1/instance/ldap_connections/" + enc(connectionId) + "/bind_password", body,
                new TypeReference<>() {
                });
    }

    public JsonNode deleteLdapBindPassword(String connectionId) {
        return t.delete("/v1/instance/ldap_connections/" + enc(connectionId) + "/bind_password",
                new TypeReference<>() {
                });
    }
}
