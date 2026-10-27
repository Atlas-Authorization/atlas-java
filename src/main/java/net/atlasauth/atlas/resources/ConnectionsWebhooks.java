package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.ConnectionsWebhooksCreateEndpointBody;
import net.atlasauth.atlas.models.ConnectionsWebhooksCreateEndpointResponse;
import net.atlasauth.atlas.models.ConnectionsWebhooksListEndpointsResponse;
import net.atlasauth.atlas.models.ConnectionsWebhooksUpdateEndpointBody;
import net.atlasauth.atlas.models.ConnectionsWebhooksVerifyBody;
import net.atlasauth.atlas.models.ConnectionsWebhooksVerifyResponse;

import java.util.List;
import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** The {@code /v1/connections} resource. */
public final class ConnectionsWebhooks {

    private final Transport t;

    public ConnectionsWebhooks(Transport t) {
        this.t = t;
    }

    /** {@code POST /v1/connections/{id}/webhooks/verify} - Verify a provider webhook signature with the connection's secret (the endpoint's own signing secret, else the connection's hmac_secret, else its OAuth client secret). Schemes: hubspot_v3 (needs method and url), shopify_hmac, intercom, zoom, slack_v0, calendly, stripe, generic_hmac_sha256. Constant-time comparison; a signed timestamp must be within the tolerance (5 minutes, Calendly 3); a delivery id or signature already seen on the connection answers `replayed`. Send the body exactly as received (`raw_body`, or base64 with raw_body_encoding). Zoom and Slack handshakes come back in `handshake`. Needs connections:webhook_verify. Scope: {@code connections:webhook_verify}. */
    public ConnectionsWebhooksVerifyResponse verify(String id, ConnectionsWebhooksVerifyBody body) {
        return verify(id, body, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public ConnectionsWebhooksVerifyResponse verify(String id, ConnectionsWebhooksVerifyBody body, String idempotencyKey) {
        return t.request("POST", "/v1/connections/" + enc(id) + "/webhooks/verify", null, body, idempotencyKey, false, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/connections/{id}/webhook_endpoints} - Create a webhook ingress endpoint on a connection. Point the provider at `ingress_url`; Atlas verifies each delivery, refuses forged, stale and replayed ones, answers Zoom and Slack handshakes, and forwards only genuine deliveries to `forward_url` as Atlas-signed webhooks (atlas-id, atlas-timestamp, atlas-signature with `forward_secret`, returned once). Bodies over 1 MiB are refused; the ingress is rate limited. Scope: {@code organizations:write}. */
    public ConnectionsWebhooksCreateEndpointResponse createEndpoint(String id, ConnectionsWebhooksCreateEndpointBody body) {
        return createEndpoint(id, body, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public ConnectionsWebhooksCreateEndpointResponse createEndpoint(String id, ConnectionsWebhooksCreateEndpointBody body, String idempotencyKey) {
        return t.request("POST", "/v1/connections/" + enc(id) + "/webhook_endpoints", null, body, idempotencyKey, false, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/connections/{id}/webhook_endpoints} - List a connection's webhook ingress endpoints. Scope: {@code organizations:read}. */
    public ConnectionsWebhooksListEndpointsResponse listEndpoints(String id) {
        return listEndpoints(id, null, null);
    }

    /** As above, with every optional parameter. */
    public ConnectionsWebhooksListEndpointsResponse listEndpoints(String id, Long limit, String startingAfter) {
        Map<String, Object> q = Params.of().set("limit", limit).set("starting_after", startingAfter).map();
        return t.request("GET", "/v1/connections/" + enc(id) + "/webhook_endpoints", q, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code PATCH /v1/connections/{id}/webhook_endpoints/{endpointId}} - Change an endpoint's forward_url, enabled flag or signing secret. Scope: {@code organizations:write}. */
    public ConnectionsWebhooksCreateEndpointResponse updateEndpoint(String id, String endpointId, ConnectionsWebhooksUpdateEndpointBody body) {
        return t.request("PATCH", "/v1/connections/" + enc(id) + "/webhook_endpoints/" + enc(endpointId), null, body, null, false, new TypeReference<>() {
        });
    }

    /** {@code DELETE /v1/connections/{id}/webhook_endpoints/{endpointId}} - Delete a webhook ingress endpoint and its secrets. Scope: {@code organizations:write}. */
    public JsonNode deleteEndpoint(String id, String endpointId) {
        return t.request("DELETE", "/v1/connections/" + enc(id) + "/webhook_endpoints/" + enc(endpointId), null, null, null, false, new TypeReference<JsonNode>() {
        });
    }
}
