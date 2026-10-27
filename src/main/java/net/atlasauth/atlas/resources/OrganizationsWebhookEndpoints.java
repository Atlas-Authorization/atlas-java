package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.OrganizationsWebhookEndpointsCreateBody;
import net.atlasauth.atlas.models.OrganizationsWebhookEndpointsDeleteResponse;
import net.atlasauth.atlas.models.OrganizationsWebhookEndpointsDeliveriesResponse;
import net.atlasauth.atlas.models.OrganizationsWebhookEndpointsListResponse;
import net.atlasauth.atlas.models.OrganizationsWebhookEndpointsListResponseDataItem;
import net.atlasauth.atlas.models.OrganizationsWebhookEndpointsRotateSecretResponse;

import java.util.List;
import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** The {@code /v1/organizations} resource. */
public final class OrganizationsWebhookEndpoints {

    private final Transport t;

    public OrganizationsWebhookEndpoints(Transport t) {
        this.t = t;
    }

    /** {@code GET /v1/organizations/{id}/webhook_endpoints} - List the organization's own webhook endpoints. Backend API. Never returns a secret. Scope: {@code webhooks:read}. */
    public OrganizationsWebhookEndpointsListResponse list(String id) {
        return list(id, null, null);
    }

    /** As above, with every optional parameter. */
    public OrganizationsWebhookEndpointsListResponse list(String id, Long limit, String startingAfter) {
        Map<String, Object> q = Params.of().set("limit", limit).set("starting_after", startingAfter).map();
        return t.request("GET", "/v1/organizations/" + enc(id) + "/webhook_endpoints", q, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/organizations/{id}/webhook_endpoints} - Register a webhook endpoint for one organization. It receives only that organization's events (membership, invitation, role, domain and organization events). The signing secret is returned in this response only. Backend API. At most 20 per organization. Scope: {@code webhooks:write}. */
    public JsonNode create(String id, OrganizationsWebhookEndpointsCreateBody body) {
        return create(id, body, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public JsonNode create(String id, OrganizationsWebhookEndpointsCreateBody body, String idempotencyKey) {
        return t.request("POST", "/v1/organizations/" + enc(id) + "/webhook_endpoints", null, body, idempotencyKey, false, new TypeReference<JsonNode>() {
        });
    }

    /** {@code GET /v1/organizations/{id}/webhook_endpoints/{endpointId}} - Retrieve one of the organization's webhook endpoints, with its delivery health. Scope: {@code webhooks:read}. */
    public OrganizationsWebhookEndpointsListResponseDataItem get(String id, String endpointId) {
        return t.request("GET", "/v1/organizations/" + enc(id) + "/webhook_endpoints/" + enc(endpointId), null, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code PATCH /v1/organizations/{id}/webhook_endpoints/{endpointId}} - Update the url, subscribed events, description or active flag of one of the organization's endpoints. Never re-reveals the secret. Scope: {@code webhooks:write}. */
    public OrganizationsWebhookEndpointsListResponseDataItem update(String id, String endpointId, OrganizationsWebhookEndpointsCreateBody body) {
        return t.request("PATCH", "/v1/organizations/" + enc(id) + "/webhook_endpoints/" + enc(endpointId), null, body, null, false, new TypeReference<>() {
        });
    }

    /** {@code DELETE /v1/organizations/{id}/webhook_endpoints/{endpointId}} - Delete one of the organization's webhook endpoints and its delivery log. An id from another organization is a 404. Scope: {@code webhooks:write}. */
    public OrganizationsWebhookEndpointsDeleteResponse delete(String id, String endpointId) {
        return t.request("DELETE", "/v1/organizations/" + enc(id) + "/webhook_endpoints/" + enc(endpointId), null, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/organizations/{id}/webhook_endpoints/{endpointId}/rotate_secret} - Rotate the signing secret. The previous secret keeps signing alongside the new one for 24 hours so the receiver can switch over without dropping events. Scope: {@code webhooks:write}. */
    public OrganizationsWebhookEndpointsRotateSecretResponse rotateSecret(String id, String endpointId) {
        return rotateSecret(id, endpointId, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public OrganizationsWebhookEndpointsRotateSecretResponse rotateSecret(String id, String endpointId, String idempotencyKey) {
        return t.request("POST", "/v1/organizations/" + enc(id) + "/webhook_endpoints/" + enc(endpointId) + "/rotate_secret", null, null, idempotencyKey, false, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/organizations/{id}/webhook_endpoints/{endpointId}/deliveries} - The endpoint's delivery log: payload, response and retry state for each attempt, filterable by status, event type and time. Scope: {@code webhooks:read}. */
    public OrganizationsWebhookEndpointsDeliveriesResponse deliveries(String id, String endpointId) {
        return deliveries(id, endpointId, null, null, null, null, null, null, null);
    }

    /** As above, with every optional parameter. */
    public OrganizationsWebhookEndpointsDeliveriesResponse deliveries(String id, String endpointId, Long limit, String startingAfter, String status, String eventType, String since, String until, String trace) {
        Map<String, Object> q = Params.of().set("limit", limit).set("starting_after", startingAfter).set("status", status).set("event_type", eventType).set("since", since).set("until", until).set("trace", trace).map();
        return t.request("GET", "/v1/organizations/" + enc(id) + "/webhook_endpoints/" + enc(endpointId) + "/deliveries", q, null, null, false, new TypeReference<>() {
        });
    }
}
