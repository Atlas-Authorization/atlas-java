package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code POST /v1/flows}. Required: name, definition. */
public final class JourneysCreateBody {
    /** Required. */
    public String name;
    public String description;
    /** The journey for every sign-in without an application-specific one (only one is default). */
    public Boolean isDefault;
    /** OAuth client ids whose sign-ins use this journey instead of the default. */
    public List<String> applications;
    public Boolean enabled;
    /** Required. */
    public Map<String, Object> definition;
    /** Make the new version active (default true). */
    public Boolean activate;

    public JourneysCreateBody() {
    }

    public JourneysCreateBody name(String v) { this.name = v; return this; }
    public JourneysCreateBody description(String v) { this.description = v; return this; }
    public JourneysCreateBody isDefault(Boolean v) { this.isDefault = v; return this; }
    public JourneysCreateBody applications(List<String> v) { this.applications = v; return this; }
    public JourneysCreateBody enabled(Boolean v) { this.enabled = v; return this; }
    public JourneysCreateBody definition(Map<String, Object> v) { this.definition = v; return this; }
    public JourneysCreateBody activate(Boolean v) { this.activate = v; return this; }
}
