package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code POST /v1/integrations/{integration}/card/actions/{action}}. Required: sub. */
public final class IntegrationsRunCardActionBody {
    /** Required. */
    public String sub;
    public String iss;
    /** Who pressed the button in the integrated service (recorded in the audit log). */
    public Map<String, Object> agent;

    public IntegrationsRunCardActionBody() {
    }

    public IntegrationsRunCardActionBody sub(String v) { this.sub = v; return this; }
    public IntegrationsRunCardActionBody iss(String v) { this.iss = v; return this; }
    public IntegrationsRunCardActionBody agent(Map<String, Object> v) { this.agent = v; return this; }
}
