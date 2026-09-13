package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * The per-flow kill switches for an instance. Used both as a response view and as
 * an update input. Its wire keys are already camelCase, so each is pinned with
 * {@link JsonProperty} against the globally snake_case mapper.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public final class InstanceKillSwitches {
    /** Refuse sign-ups for this instance only. */
    @JsonProperty("disableSignUps")
    public Boolean disableSignUps;
    /** Refuse all sign-ins for this instance — the heaviest per-tenant control. */
    @JsonProperty("disableSignIns")
    public Boolean disableSignIns;
    /** Require a bot challenge for this instance only. */
    @JsonProperty("forceChallenge")
    public Boolean forceChallenge;
    /** Provider keys temporarily refused (e.g. a provider outage). */
    @JsonProperty("disabledProviders")
    public List<String> disabledProviders;

    public InstanceKillSwitches() {
    }

    public InstanceKillSwitches disableSignUps(Boolean v) { this.disableSignUps = v; return this; }
    public InstanceKillSwitches disableSignIns(Boolean v) { this.disableSignIns = v; return this; }
    public InstanceKillSwitches forceChallenge(Boolean v) { this.forceChallenge = v; return this; }
    public InstanceKillSwitches disabledProviders(List<String> v) { this.disabledProviders = v; return this; }
}
