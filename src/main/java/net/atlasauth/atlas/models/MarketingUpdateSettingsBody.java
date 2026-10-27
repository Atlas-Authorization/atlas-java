package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code PUT /v1/marketing_settings}. */
public final class MarketingUpdateSettingsBody {
    public Map<String, Object> capture;
    public Map<String, Object> scripts;

    public MarketingUpdateSettingsBody() {
    }

    public MarketingUpdateSettingsBody capture(Map<String, Object> v) { this.capture = v; return this; }
    public MarketingUpdateSettingsBody scripts(Map<String, Object> v) { this.scripts = v; return this; }
}
