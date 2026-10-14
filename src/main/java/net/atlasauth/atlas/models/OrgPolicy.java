package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * An organization's security policy overrides, typed. Only the overrides that are
 * set appear (unset fields are {@code null}); keys this SDK does not model yet are
 * kept in {@link #extra()}. The wire keys are camelCase.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public final class OrgPolicy {
    /** Members must have MFA enrolled. */
    @JsonProperty("requireMfa")
    public Boolean requireMfa;
    /** Members must sign in through the organization's SSO connection. */
    @JsonProperty("ssoRequired")
    public Boolean ssoRequired;
    /** Session idle timeout for members, in milliseconds. */
    @JsonProperty("sessionIdleOverrideMs")
    public Long sessionIdleOverrideMs;
    /**
     * The sign-in methods members may use (e.g. {@code "password"}, {@code "passkey"},
     * {@code "oauth"}, {@code "oauth:google"}, {@code "saml"}, {@code "magic_link"}).
     * Empty or absent means all methods.
     */
    @JsonProperty("allowedSignInMethods")
    public List<String> allowedSignInMethods;
    /** CIDR allow-list gating member sign-in. */
    @JsonProperty("ipAllowlist")
    public List<String> ipAllowlist;
    /** Absolute session lifetime cap for members, in seconds. */
    @JsonProperty("maxSessionAgeSeconds")
    public Long maxSessionAgeSeconds;
    /** The organization's end-user (reader) pool settings. */
    @JsonProperty("endUserPool")
    public EndUserPoolPolicy endUserPool;

    @com.fasterxml.jackson.annotation.JsonIgnore
    private final Map<String, Object> extra = new LinkedHashMap<>();

    /** Policy keys this SDK version does not model, as returned by the server. */
    public Map<String, Object> extra() {
        return extra;
    }

    @JsonAnySetter
    void putExtra(String key, Object value) {
        extra.put(key, value);
    }
}
