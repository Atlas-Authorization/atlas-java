package net.atlasauth.atlas.models;

import java.util.Map;

/** Body for {@code POST /v1/actions}. */
public final class CreateActionBody {
    public String name;
    /** {@code post_login} | {@code pre_user_registration} | {@code post_user_registration}. */
    public String trigger;
    public String code;
    public Boolean enabled;
    /** Write-only, encrypted at rest, never read back. */
    public Map<String, String> secrets;

    public CreateActionBody() {
    }

    public CreateActionBody(String name, String trigger, String code) {
        this.name = name;
        this.trigger = trigger;
        this.code = code;
    }

    public CreateActionBody name(String v) { this.name = v; return this; }
    public CreateActionBody trigger(String v) { this.trigger = v; return this; }
    public CreateActionBody code(String v) { this.code = v; return this; }
    public CreateActionBody enabled(Boolean v) { this.enabled = v; return this; }
    public CreateActionBody secrets(Map<String, String> v) { this.secrets = v; return this; }
}
