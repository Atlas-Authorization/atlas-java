package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Body for {@code PATCH /v1/instance/hooks/before_user_create}: only the fields set change.
 * Turning it on needs {@code url} and {@code signingSecret}. Clear a field with
 * {@link #clear(String)} ({@code url}, {@code signing_secret}).
 */
public final class UpdateBeforeUserCreateHookBody {
    public Boolean enabled;
    /** https (http only for localhost on a development instance). */
    public String url;
    /** Write-only, 16-512 characters. */
    public String signingSecret;
    /** 500-5000. */
    public Integer timeoutMs;

    @JsonIgnore
    private final Map<String, Object> additionalFields = new LinkedHashMap<>();

    public UpdateBeforeUserCreateHookBody() {
    }

    public UpdateBeforeUserCreateHookBody enabled(Boolean v) { this.enabled = v; return this; }
    public UpdateBeforeUserCreateHookBody url(String v) { this.url = v; return this; }
    public UpdateBeforeUserCreateHookBody signingSecret(String v) { this.signingSecret = v; return this; }
    public UpdateBeforeUserCreateHookBody timeoutMs(Integer v) { this.timeoutMs = v; return this; }

    /** Clear a field by its wire name: it is sent as {@code null}. */
    public UpdateBeforeUserCreateHookBody clear(String key) { this.additionalFields.put(key, null); return this; }

    @JsonAnyGetter
    public Map<String, Object> additionalFields() { return additionalFields; }
}
