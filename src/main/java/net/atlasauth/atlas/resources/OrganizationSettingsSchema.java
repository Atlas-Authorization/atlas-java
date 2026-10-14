package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.OrganizationSettingsSchemaDocument;

import java.util.List;
import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** The instance-wide JSON Schema for organization settings ({@code /v1/organization_settings_schema}). */
public final class OrganizationSettingsSchema {

    private final Transport t;

    public OrganizationSettingsSchema(Transport t) {
        this.t = t;
    }

    /** {@code GET /v1/organization_settings_schema} — Fetch the instance-wide JSON Schema registered for organization settings. */
    public OrganizationSettingsSchemaDocument get() {
        return t.get("/v1/organization_settings_schema", new TypeReference<>() {
        });
    }

    /** {@code POST /v1/organization_settings_schema} — Register the instance-wide JSON Schema for organization settings (first registration). */
    public OrganizationSettingsSchemaDocument create(Map<String, Object> schema) {
        Map<String, Object> payload = Params.of().set("schema", schema).map();
        return t.post("/v1/organization_settings_schema", payload, new TypeReference<>() {
        });
    }

    /** {@code PUT /v1/organization_settings_schema} — Replace the instance-wide organization-settings JSON Schema. */
    public OrganizationSettingsSchemaDocument replace(Map<String, Object> schema) {
        Map<String, Object> payload = Params.of().set("schema", schema).map();
        return t.put("/v1/organization_settings_schema", payload, new TypeReference<>() {
        });
    }
}
