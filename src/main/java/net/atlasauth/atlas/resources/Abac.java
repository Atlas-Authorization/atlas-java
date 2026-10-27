package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.AbacAccessibleResourcesBody;
import net.atlasauth.atlas.models.AbacAccessibleResourcesResponse;
import net.atlasauth.atlas.models.AbacAuthzenEvaluationBody;
import net.atlasauth.atlas.models.AbacAuthzenEvaluationResponse;
import net.atlasauth.atlas.models.AbacAuthzenEvaluationsResponse;
import net.atlasauth.atlas.models.AbacAuthzenSearchActionBody;
import net.atlasauth.atlas.models.AbacAuthzenSearchActionResponse;
import net.atlasauth.atlas.models.AbacAuthzenSearchResourceBody;
import net.atlasauth.atlas.models.AbacAuthzenSearchSubjectBody;
import net.atlasauth.atlas.models.AbacAuthzenSearchSubjectResponse;
import net.atlasauth.atlas.models.AbacCreateBody;
import net.atlasauth.atlas.models.AbacDiffResponse;
import net.atlasauth.atlas.models.AbacDiffSourcesBody;
import net.atlasauth.atlas.models.AbacEvaluateBody;
import net.atlasauth.atlas.models.AbacEvaluateResponse;
import net.atlasauth.atlas.models.AbacListResponse;
import net.atlasauth.atlas.models.AbacListResponseDataItem;
import net.atlasauth.atlas.models.AbacPartialEvaluateBody;
import net.atlasauth.atlas.models.AbacPartialEvaluateResponse;
import net.atlasauth.atlas.models.AbacTestBody;
import net.atlasauth.atlas.models.AbacTestResponse;
import net.atlasauth.atlas.models.AbacUpdateBody;
import net.atlasauth.atlas.models.AbacVersionsResponse;

import java.util.List;
import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** The {@code /v1/abac_policies} resource. */
public final class Abac {

    private final Transport t;

    public Abac(Transport t) {
        this.t = t;
    }

    /** {@code GET /v1/abac_policies} - List the ABAC policies (current version of each). Scope: {@code abac:read}. */
    public AbacListResponse list() {
        return list(null, null);
    }

    /** As above, with every optional parameter. */
    public AbacListResponse list(Long limit, String startingAfter) {
        Map<String, Object> q = Params.of().set("limit", limit).set("starting_after", startingAfter).map();
        return t.request("GET", "/v1/abac_policies", q, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/abac_policies} - Create an ABAC policy from `source` written in the Cedar-subset policy language (permit/forbid with when/unless over principal, action, resource and context attributes; `fga.check(...)` consults the policy's FGA store). Source that does not parse is refused with the character position; with an entity schema set it is also type-checked and every problem is reported. Optional `tests` are saved with version 1. Scope: {@code abac:write}. */
    public AbacListResponseDataItem create(AbacCreateBody body) {
        return create(body, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public AbacListResponseDataItem create(AbacCreateBody body, String idempotencyKey) {
        return t.request("POST", "/v1/abac_policies", null, body, idempotencyKey, false, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/abac_policies/{id}} - Read one ABAC policy with its current source and tests. Scope: {@code abac:read}. */
    public AbacListResponseDataItem get(String id) {
        return t.request("GET", "/v1/abac_policies/" + enc(id), null, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code PATCH /v1/abac_policies/{id}} - Update an ABAC policy. Sending `source` appends a new version and makes it current; `current_version` (without `source`) rolls back to an earlier version; name, description, enabled and fga_store_id change in place. Scope: {@code abac:write}. */
    public AbacListResponseDataItem update(String id, AbacUpdateBody body) {
        return t.request("PATCH", "/v1/abac_policies/" + enc(id), null, body, null, false, new TypeReference<>() {
        });
    }

    /** {@code DELETE /v1/abac_policies/{id}} - Delete an ABAC policy and all its versions. Scope: {@code abac:write}. */
    public JsonNode delete(String id) {
        return t.request("DELETE", "/v1/abac_policies/" + enc(id), null, null, null, false, new TypeReference<JsonNode>() {
        });
    }

    /** {@code GET /v1/abac_policies/{id}/versions} - List every saved version of an ABAC policy, newest first. Scope: {@code abac:read}. */
    public AbacVersionsResponse versions(String id) {
        return versions(id, null, null);
    }

    /** As above, with every optional parameter. */
    public AbacVersionsResponse versions(String id, Long limit, String startingAfter) {
        Map<String, Object> q = Params.of().set("limit", limit).set("starting_after", startingAfter).map();
        return t.request("GET", "/v1/abac_policies/" + enc(id) + "/versions", q, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/abac_policies/{id}/test} - Run test cases against a policy version (default: the current one), or against a draft `source` and `tests` sent in the body, without saving anything. Each test is a request plus the expected decision; the policy is evaluated on its own, not with the rest of the set. The response also carries `coverage`: which rules and `when`/`unless` clauses the tests exercised (a clause no test saw both true and false is listed as a gap), with line numbers. Scope: {@code abac:read}. */
    public AbacTestResponse test(String id, AbacTestBody body) {
        return test(id, body, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public AbacTestResponse test(String id, AbacTestBody body, String idempotencyKey) {
        return t.request("POST", "/v1/abac_policies/" + enc(id) + "/test", null, body, idempotencyKey, false, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/abac/evaluate} - Policy decision point: ask whether `principal` may perform `action` on `resource` given `context` (and optional extra `entities` for hierarchy). All enabled policies are evaluated together with Cedar semantics: a matching forbid wins, otherwise a matching permit allows, otherwise deny. Each decision is written to the event feed (type authz.decision), so log streams carry it. Scope: {@code abac:evaluate}. */
    public AbacEvaluateResponse evaluate(AbacEvaluateBody body) {
        return evaluate(body, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public AbacEvaluateResponse evaluate(AbacEvaluateBody body, String idempotencyKey) {
        return t.request("POST", "/v1/abac/evaluate", null, body, idempotencyKey, false, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/abac/partial_evaluate} - Partial evaluation (data filtering): ask what is left of the decision when an entity is unknown. With a known principal, an action and a resource TYPE, the response is a residual `filter` over the resource attributes (or constant `true`/`false`) that is equivalent to evaluating the policies on each resource. `unknowns: ["principal"]` answers "who may do this on that resource". Relationship conditions (`fga.check`) are resolved to id lists from the policy's FGA store unless `resolve_relations` is false. `output` asks for a ready-made parameterised SQL WHERE (`sql`: dialect and a field-to-column map) and/or a Prisma where object (`prisma`: a field map). Policies that build sets from unknown attributes cannot be expressed as a filter and are refused with a message naming the policy. Scope: {@code abac:evaluate}. */
    public AbacPartialEvaluateResponse partialEvaluate(AbacPartialEvaluateBody body) {
        return partialEvaluate(body, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public AbacPartialEvaluateResponse partialEvaluate(AbacPartialEvaluateBody body, String idempotencyKey) {
        return t.request("POST", "/v1/abac/partial_evaluate", null, body, idempotencyKey, false, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/abac/accessible_resources} - List the resources of a type a principal may perform an action on, paged (`limit`, `starting_after`; the response carries `has_more` and `next_cursor`). Candidates are the Atlas resources of that type (their metadata are the attributes) or the `candidates` you send. The policies are reduced to one filter and applied to every candidate; `method` says whether that worked or each candidate was evaluated in full. Scope: {@code abac:evaluate}. */
    public AbacAccessibleResourcesResponse accessibleResources(AbacAccessibleResourcesBody body) {
        return accessibleResources(body, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public AbacAccessibleResourcesResponse accessibleResources(AbacAccessibleResourcesBody body, String idempotencyKey) {
        return t.request("POST", "/v1/abac/accessible_resources", null, body, idempotencyKey, false, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/abac_policies/{id}/diff} - Compare two versions of a policy: a line diff of the source and, unless `semantic=false`, the requests whose decision changes. The semantic part enumerates a bounded set of requests built from the attributes, groups and actions the policies test (from the entity schema when one is set), and lists those that come out differently, with newly-allowed and newly-denied counts per action. `exhaustive` says whether every request in the space was compared or the space was sampled. Scope: {@code abac:read}. */
    public AbacDiffResponse diff(String id) {
        return diff(id, null, null, null, null, null);
    }

    /** As above, with every optional parameter. */
    public AbacDiffResponse diff(String id, Long from, Long to, Boolean semantic, Long maxRequests, Long maxChanges) {
        Map<String, Object> q = Params.of().set("from", from).set("to", to).set("semantic", semantic).set("max_requests", maxRequests).set("max_changes", maxChanges).map();
        return t.request("GET", "/v1/abac_policies/" + enc(id) + "/diff", q, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/abac/diff} - The same comparison for two policy sources you send (a draft against the saved version, or two templates), without saving anything. Scope: {@code abac:read}. */
    public AbacDiffResponse diffSources(AbacDiffSourcesBody body) {
        return diffSources(body, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public AbacDiffResponse diffSources(AbacDiffSourcesBody body, String idempotencyKey) {
        return t.request("POST", "/v1/abac/diff", null, body, idempotencyKey, false, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/authzen/access/v1/evaluation} - AuthZEN Access Evaluation: `&#123;subject, resource, action, context&#125;` -&gt; `&#123;decision: boolean&#125;`. Entity types map to PascalCase policy types. Scope: {@code abac:evaluate}. */
    public AbacAuthzenEvaluationResponse authzenEvaluation(AbacAuthzenEvaluationBody body) {
        return authzenEvaluation(body, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public AbacAuthzenEvaluationResponse authzenEvaluation(AbacAuthzenEvaluationBody body, String idempotencyKey) {
        return t.request("POST", "/v1/authzen/access/v1/evaluation", null, body, idempotencyKey, false, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/authzen/access/v1/evaluations} - AuthZEN Access Evaluations (batch, up to 100): shared defaults plus an `evaluations` array, with `options.evaluations_semantic` of execute_all (default), deny_on_first_deny or permit_on_first_permit. Scope: {@code abac:evaluate}. */
    public AbacAuthzenEvaluationsResponse authzenEvaluations(AbacAuthzenEvaluationBody body) {
        return authzenEvaluations(body, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public AbacAuthzenEvaluationsResponse authzenEvaluations(AbacAuthzenEvaluationBody body, String idempotencyKey) {
        return t.request("POST", "/v1/authzen/access/v1/evaluations", null, body, idempotencyKey, false, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/authzen/access/v1/search/subject} - AuthZEN Subject Search: which subjects of `subject.type` may perform `action` on `resource` (give the resource a `type` and `id`). Answers from the same ABAC policies as an evaluation, reduced once by partial evaluation and applied to the candidates Atlas knows: your users (type `user`, their public metadata as attributes) and the users in the FGA stores the policies consult. `page.limit` and `page.token` page the result; the response carries `page.next_token` (empty on the last page), `count` and `total`. Scope: {@code abac:evaluate}. */
    public AbacAuthzenSearchSubjectResponse authzenSearchSubject(AbacAuthzenSearchSubjectBody body) {
        return authzenSearchSubject(body, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public AbacAuthzenSearchSubjectResponse authzenSearchSubject(AbacAuthzenSearchSubjectBody body, String idempotencyKey) {
        return t.request("POST", "/v1/authzen/access/v1/search/subject", null, body, idempotencyKey, false, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/authzen/access/v1/search/resource} - AuthZEN Resource Search: which resources of `resource.type` the `subject` may perform `action` on. Candidates are your synced Atlas resources of that type (their metadata are the attributes) and the objects of that type in the FGA stores the policies consult. Paged with `page.limit` and `page.token`. Scope: {@code abac:evaluate}. */
    public AbacAuthzenSearchSubjectResponse authzenSearchResource(AbacAuthzenSearchResourceBody body) {
        return authzenSearchResource(body, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public AbacAuthzenSearchSubjectResponse authzenSearchResource(AbacAuthzenSearchResourceBody body, String idempotencyKey) {
        return t.request("POST", "/v1/authzen/access/v1/search/resource", null, body, idempotencyKey, false, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/authzen/access/v1/search/action} - AuthZEN Action Search: which actions the `subject` may perform on the `resource`. The actions considered are the ones the policies name (and the entity schema declares), each evaluated in full; the result lists the permitted action names. Scope: {@code abac:evaluate}. */
    public AbacAuthzenSearchActionResponse authzenSearchAction(AbacAuthzenSearchActionBody body) {
        return authzenSearchAction(body, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public AbacAuthzenSearchActionResponse authzenSearchAction(AbacAuthzenSearchActionBody body, String idempotencyKey) {
        return t.request("POST", "/v1/authzen/access/v1/search/action", null, body, idempotencyKey, false, new TypeReference<>() {
        });
    }

    /** The {@code templates} sub-resource. */
    public AbacTemplates templates() {
        return new AbacTemplates(t);
    }

    /** The {@code links} sub-resource. */
    public AbacLinks links() {
        return new AbacLinks(t);
    }

    /** The {@code schema} sub-resource. */
    public AbacSchema schema() {
        return new AbacSchema(t);
    }
}
