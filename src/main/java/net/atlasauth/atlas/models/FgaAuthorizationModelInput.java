package net.atlasauth.atlas.models;

import java.util.List;
import java.util.Map;

/**
 * An OpenFGA authorization model to persist (validated before storage).
 * {@code typeDefinitions} is required; {@code schemaVersion} defaults to
 * {@code 1.1} when omitted. Fluent setters return {@code this}.
 */
public final class FgaAuthorizationModelInput {
    public List<Object> typeDefinitions;
    public String schemaVersion;
    public Map<String, Object> conditions;

    public FgaAuthorizationModelInput() {
    }

    public FgaAuthorizationModelInput(List<Object> typeDefinitions) {
        this.typeDefinitions = typeDefinitions;
    }

    public FgaAuthorizationModelInput typeDefinitions(List<Object> v) { this.typeDefinitions = v; return this; }
    public FgaAuthorizationModelInput schemaVersion(String v) { this.schemaVersion = v; return this; }
    public FgaAuthorizationModelInput conditions(Map<String, Object> v) { this.conditions = v; return this; }
}
