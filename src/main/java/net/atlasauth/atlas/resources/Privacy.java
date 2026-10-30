package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.PrivacyIntegration;
import net.atlasauth.atlas.models.PrivacyIntegrationConnectBody;
import net.atlasauth.atlas.models.CreatePrivacyRequestBody;
import net.atlasauth.atlas.models.PrivacyLookupBody;
import net.atlasauth.atlas.models.PrivacyLookupResult;
import net.atlasauth.atlas.models.PrivacyRequest;
import net.atlasauth.atlas.models.SetPrivacyOrchestratorBody;

import static net.atlasauth.atlas.http.Transport.enc;

/**
 * The data-rights executor protocol (v2), {@code /v1/privacy}: the routes a DSAR orchestrator calls to find a
 * person and carry out a request against their identity data (needs a key with the {@code privacy:execute}
 * scope; {@code privacy:grants:revoke} lets an erasure also revoke the Sign in with Atlas grant), and the
 * instance's own orchestrator connection ({@link #orchestrator()}), where Atlas opens a case when an account
 * deletion is scheduled.
 */
public final class Privacy {
    private final Transport t;
    private final Orchestrator orchestrator;
    private final Integration integration;

    public Privacy(Transport t) {
        this.t = t;
        this.orchestrator = new Orchestrator(t);
        this.integration = new Integration(t);
    }

    /** The consent manager, orchestrator, completion webhook and executor key in one view ({@code /v1/instance/privacy_integrations}). */
    public Integration integration() {
        return integration;
    }

    /** The instance's data-rights orchestrator connection. */
    public Orchestrator orchestrator() {
        return orchestrator;
    }

    /** {@code GET /v1/privacy} — What this executor speaks (public). */
    public JsonNode discover() {
        return t.get("/v1/privacy", new TypeReference<>() {
        });
    }

    /** {@code GET /v1/privacy/presets/{id}} — A ready-made orchestrator connection profile such as {@code cookiemunch} (public). */
    public JsonNode preset(String id) {
        return t.get("/v1/privacy/presets/" + enc(id), new TypeReference<>() {
        });
    }

    /** {@code POST /v1/privacy/lookup} — Find a person ({@code atlas.sub} wins, then {@code atlas.ext}, then the verified emails). */
    public PrivacyLookupResult lookup(PrivacyLookupBody body) {
        return t.post("/v1/privacy/lookup", body, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/privacy/requests} — Open a request, or return the existing one for the same case. */
    public PrivacyRequest createRequest(CreatePrivacyRequestBody body) {
        return createRequest(body, null);
    }

    /** {@link #createRequest(CreatePrivacyRequestBody)} with an {@code Idempotency-Key}. */
    public PrivacyRequest createRequest(CreatePrivacyRequestBody body, String idempotencyKey) {
        return t.post("/v1/privacy/requests", body, idempotencyKey, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/privacy/requests/{id}} — One request. */
    public PrivacyRequest getRequest(String id) {
        return t.get("/v1/privacy/requests/" + enc(id), new TypeReference<>() {
        });
    }

    /** {@code POST /v1/privacy/requests/{id}/cancel} — Cancel a request before it runs: nothing is erased, the account stays. */
    public PrivacyRequest cancelRequest(String id) {
        return t.post("/v1/privacy/requests/" + enc(id) + "/cancel", null, new TypeReference<>() {
        });
    }

    /** {@code /v1/instance/privacy_orchestrator} — where Atlas opens a data-rights case. Credentials are never returned; a signing secret appears once, on the write that mints it. */
    public static final class Orchestrator {
        private final Transport t;

        Orchestrator(Transport t) {
            this.t = t;
        }

        /** {@code GET /v1/instance/privacy_orchestrator} — The orchestrator connection. */
        public JsonNode get() {
            return t.get("/v1/instance/privacy_orchestrator", new TypeReference<>() {
            });
        }

        /** {@code PUT /v1/instance/privacy_orchestrator} — Connect or update the orchestrator; new secrets come back once. */
        public JsonNode set(SetPrivacyOrchestratorBody body) {
            return t.put("/v1/instance/privacy_orchestrator", body, new TypeReference<>() {
            });
        }

        /** {@code DELETE /v1/instance/privacy_orchestrator} — Remove the connection. */
        public JsonNode delete() {
            return t.delete("/v1/instance/privacy_orchestrator", new TypeReference<>() {
            });
        }
    }

    /** {@code /v1/instance/privacy_integrations}. */
    public static final class Integration {
        private final Transport t;

        Integration(Transport t) {
            this.t = t;
        }

        /** {@code GET /v1/instance/privacy_integrations}. */
        public PrivacyIntegration get() {
            return t.get("/v1/instance/privacy_integrations", new TypeReference<>() {
            });
        }

        /**
         * {@code POST /v1/instance/privacy_integrations/connect} - wire the consent manager, orchestrator, completion
         * webhook and executor key in one idempotent call. The executor key's {@code secret} comes back once; a re-run
         * answers {@code executorKey.idempotentReplay}.
         */
        public PrivacyIntegration connect(PrivacyIntegrationConnectBody body) {
            return t.post("/v1/instance/privacy_integrations/connect", body, new TypeReference<>() {
            });
        }
    }
}
