package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.AbacSchemaGetResponse;
import net.atlasauth.atlas.models.AbacSchemaSetBody;
import net.atlasauth.atlas.models.AbacSchemaValidateBody;
import net.atlasauth.atlas.models.AbacSchemaValidateResponse;

import java.util.List;
import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** The {@code /v1/abac_schema} resource. */
public final class AbacSchema {

    private final Transport t;

    public AbacSchema(Transport t) {
        this.t = t;
    }

    /** {@code GET /v1/abac_schema} - The instance's ABAC entity schema (`schema` is null and `version` 0 when none is set). Scope: {@code abac:read}. */
    public AbacSchemaGetResponse get() {
        return t.request("GET", "/v1/abac_schema", null, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code PUT /v1/abac_schema} - Set the ABAC entity schema: `entity_types` (attributes with `boolean`, `long`, `string`, `set`, `entity` and `record` types, `required: false` for optional ones, and `member_of`) and `actions` (`applies_to` principal and resource types and a `context`). Every problem is reported with its path. Once set, policies and templates are type-checked when saved: unknown attributes, optional attributes read without a `has` guard, wrong operand types, unknown entity types and actions, and actions that do not apply to the policy's principal and resource types. The response lists the errors the schema would give the policies and links already stored. Scope: {@code abac:write}. */
    public AbacSchemaGetResponse set(AbacSchemaSetBody body) {
        return t.request("PUT", "/v1/abac_schema", null, body, null, false, new TypeReference<>() {
        });
    }

    /** {@code DELETE /v1/abac_schema} - Remove the entity schema; policies are no longer type-checked. Scope: {@code abac:write}. */
    public JsonNode delete() {
        return t.request("DELETE", "/v1/abac_schema", null, null, null, false, new TypeReference<JsonNode>() {
        });
    }

    /** {@code POST /v1/abac_schema/validate} - Type-check policy (or, with `template: true`, template) source against the saved schema, or one sent as `schema`, without saving anything. Returns every issue with its policy number, clause, character offset, line and column. A syntax error is reported as an issue too. Scope: {@code abac:read}. */
    public AbacSchemaValidateResponse validate(AbacSchemaValidateBody body) {
        return validate(body, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public AbacSchemaValidateResponse validate(AbacSchemaValidateBody body, String idempotencyKey) {
        return t.request("POST", "/v1/abac_schema/validate", null, body, idempotencyKey, false, new TypeReference<>() {
        });
    }
}
