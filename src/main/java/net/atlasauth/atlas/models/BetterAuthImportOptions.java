package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** What a Better Auth import does. Every field is optional; the API refuses an unknown one. */
@JsonIgnoreProperties(ignoreUnknown = true)
public final class BetterAuthImportOptions {
    /** Only these Better Auth models (e.g. {@code user}, {@code account}, {@code organization}). Default: all. */
    public List<String> models;
    /** {@code skip} (default) or {@code link}: a user whose verified email belongs to an Atlas user from elsewhere. */
    public String onEmailConflict;
    /** The admin plugin's default role (not carried as an Atlas app role). Default {@code user}. */
    public String defaultRole;
    /** Create an Atlas app role for each admin-plugin role that has none. Default true. */
    public Boolean createAppRoles;
    /** Carry social accounts' access and refresh tokens. Default true. */
    public Boolean importOauthTokens;
    /** {@code skip} (default) or {@code import}: passkeys registered for another relying-party id. */
    public String passkeysOnRpMismatch;
    /** Adopt Stripe subscriptions (needs the instance Stripe key). Default true. */
    public Boolean importSubscriptions;
    /** Better Auth plan name to Atlas billing plan id. */
    public Map<String, String> planMap;
    /** Publish the JWT plugin's public keys in the instance JWKS for the cutover. Default true. */
    public Boolean importJwks;
    /** How long imported JWKS keys stay published, 1–365. Default 30. */
    public Long jwksCutoverDays;
    /** Import still-valid sessions (as token hashes) for the session bridge. Default true. */
    public Boolean importSessions;
    /** Keep the sealed bundle after a successful apply. Default false. */
    public Boolean retainBundle;

    @JsonIgnore
    private final Map<String, Object> additionalFields = new LinkedHashMap<>();

    public BetterAuthImportOptions() {
    }

    public BetterAuthImportOptions models(List<String> v) { this.models = v; return this; }
    public BetterAuthImportOptions onEmailConflict(String v) { this.onEmailConflict = v; return this; }
    public BetterAuthImportOptions defaultRole(String v) { this.defaultRole = v; return this; }
    public BetterAuthImportOptions createAppRoles(Boolean v) { this.createAppRoles = v; return this; }
    public BetterAuthImportOptions importOauthTokens(Boolean v) { this.importOauthTokens = v; return this; }
    public BetterAuthImportOptions passkeysOnRpMismatch(String v) { this.passkeysOnRpMismatch = v; return this; }
    public BetterAuthImportOptions importSubscriptions(Boolean v) { this.importSubscriptions = v; return this; }
    public BetterAuthImportOptions planMap(Map<String, String> v) { this.planMap = v; return this; }
    public BetterAuthImportOptions importJwks(Boolean v) { this.importJwks = v; return this; }
    public BetterAuthImportOptions jwksCutoverDays(Long v) { this.jwksCutoverDays = v; return this; }
    public BetterAuthImportOptions importSessions(Boolean v) { this.importSessions = v; return this; }
    public BetterAuthImportOptions retainBundle(Boolean v) { this.retainBundle = v; return this; }

    /** Set any option by its wire name (one this SDK does not model yet). */
    public BetterAuthImportOptions set(String key, Object value) { this.additionalFields.put(key, value); return this; }

    @JsonAnyGetter
    public Map<String, Object> additionalFields() { return additionalFields; }
}
