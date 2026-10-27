package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code POST /v1/instance/integrations/{product}/events_secret/rotate}. */
public final class InstanceIntegrationsRotateEventsSecretBody {
    /** How long the previous secret keeps signing deliveries (default 0 = stop now). */
    public Long overlapSeconds;

    public InstanceIntegrationsRotateEventsSecretBody() {
    }

    public InstanceIntegrationsRotateEventsSecretBody overlapSeconds(Long v) { this.overlapSeconds = v; return this; }
}
