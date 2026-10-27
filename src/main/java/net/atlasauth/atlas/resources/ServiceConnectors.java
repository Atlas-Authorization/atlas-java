package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.ServiceConnectorsCatalogResponse;
import net.atlasauth.atlas.models.ServiceConnectorsCreateBody;
import net.atlasauth.atlas.models.ServiceConnectorsDeleteResponse;
import net.atlasauth.atlas.models.ServiceConnectorsInvokeBody;
import net.atlasauth.atlas.models.ServiceConnectorsInvokeResponse;
import net.atlasauth.atlas.models.ServiceConnectorsListResponse;
import net.atlasauth.atlas.models.ServiceConnectorsListResponseDataItem;
import net.atlasauth.atlas.models.ServiceConnectorsUpdateBody;

import java.util.List;
import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** The {@code /v1/service_connectors} resource. */
public final class ServiceConnectors {

    private final Transport t;

    public ServiceConnectors(Transport t) {
        this.t = t;
    }

    /** {@code GET /v1/service_connector_catalog} - The services you can connect: for each kind the credential fields, the config fields and the operations with their inputs. Messaging (Twilio, SendGrid, Slack), CRM (HubSpot, Salesforce), analytics (Segment), fraud (Fingerprint, Arkose Labs, Forter, Sardine), identity verification (Persona, Onfido, Veriff, Stripe Identity) and a generic HTTPS connector. Scope: {@code service_connectors:read}. */
    public ServiceConnectorsCatalogResponse catalog() {
        return catalog(null, null);
    }

    /** As above, with every optional parameter. */
    public ServiceConnectorsCatalogResponse catalog(Long limit, String startingAfter) {
        Map<String, Object> q = Params.of().set("limit", limit).set("starting_after", startingAfter).map();
        return t.request("GET", "/v1/service_connector_catalog", q, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/service_connectors} - List the instance's service connectors. Credentials are never returned. Scope: {@code service_connectors:read}. */
    public ServiceConnectorsListResponse list() {
        return list(null, null);
    }

    /** As above, with every optional parameter. */
    public ServiceConnectorsListResponse list(Long limit, String startingAfter) {
        Map<String, Object> q = Params.of().set("limit", limit).set("starting_after", startingAfter).map();
        return t.request("GET", "/v1/service_connectors", q, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/service_connectors} - Connect a service. `credentials` are checked against the kind's fields and stored in the vault (never returned again); `config` holds the non-secret settings. 409 when the name is taken. Scope: {@code service_connectors:write}. */
    public ServiceConnectorsListResponseDataItem create(ServiceConnectorsCreateBody body) {
        return create(body, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public ServiceConnectorsListResponseDataItem create(ServiceConnectorsCreateBody body, String idempotencyKey) {
        return t.request("POST", "/v1/service_connectors", null, body, idempotencyKey, false, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/service_connectors/{id}} - Read one service connector. Scope: {@code service_connectors:read}. */
    public ServiceConnectorsListResponseDataItem get(String id) {
        return t.request("GET", "/v1/service_connectors/" + enc(id), null, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code PATCH /v1/service_connectors/{id}} - Rename, enable or disable a connector, merge new `config` values, or replace individual `credentials` (written as a new vault version; the others are kept). Scope: {@code service_connectors:write}. */
    public ServiceConnectorsListResponseDataItem update(String id, ServiceConnectorsUpdateBody body) {
        return t.request("PATCH", "/v1/service_connectors/" + enc(id), null, body, null, false, new TypeReference<>() {
        });
    }

    /** {@code DELETE /v1/service_connectors/{id}} - Delete a connector and its stored credentials. Scope: {@code service_connectors:write}. */
    public ServiceConnectorsDeleteResponse delete(String id) {
        return t.request("DELETE", "/v1/service_connectors/" + enc(id), null, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/service_connectors/{id}/invoke} - Run one operation of the connector (send a message, read a fraud signal, start an identity check). `input` is checked against the operation's fields. A refusal or failure from the service is 502 CONNECTOR_UPSTREAM_ERROR with its own message. Each call is audited without its input or output. Scope: {@code service_connectors:invoke}. */
    public ServiceConnectorsInvokeResponse invoke(String id, ServiceConnectorsInvokeBody body) {
        return invoke(id, body, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public ServiceConnectorsInvokeResponse invoke(String id, ServiceConnectorsInvokeBody body, String idempotencyKey) {
        return t.request("POST", "/v1/service_connectors/" + enc(id) + "/invoke", null, body, idempotencyKey, false, new TypeReference<>() {
        });
    }
}
