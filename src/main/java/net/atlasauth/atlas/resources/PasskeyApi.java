package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.PasskeyApiDeleteResponse;
import net.atlasauth.atlas.models.PasskeyApiListResponse;
import net.atlasauth.atlas.models.PasskeyApiLoginFinalizeBody;
import net.atlasauth.atlas.models.PasskeyApiLoginFinalizeResponse;
import net.atlasauth.atlas.models.PasskeyApiLoginInitBody;
import net.atlasauth.atlas.models.PasskeyApiLoginInitResponse;
import net.atlasauth.atlas.models.PasskeyApiRegistrationFinalizeBody;
import net.atlasauth.atlas.models.PasskeyApiRegistrationFinalizeResponse;
import net.atlasauth.atlas.models.PasskeyApiRegistrationInitBody;
import net.atlasauth.atlas.models.PasskeyApiRegistrationInitResponse;
import net.atlasauth.atlas.models.PasskeyApiUsageResponse;

import java.util.List;
import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** The {@code /v1/passkey_api} resource. */
public final class PasskeyApi {

    private final Transport t;

    public PasskeyApi(Transport t) {
        this.t = t;
    }

    /** {@code POST /v1/passkey_api/registration/init} - Start registering a passkey for one of your users. Returns WebAuthn creation options for the browser. Scope: {@code passkey_api:write}. */
    public PasskeyApiRegistrationInitResponse registrationInit(PasskeyApiRegistrationInitBody body) {
        return registrationInit(body, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public PasskeyApiRegistrationInitResponse registrationInit(PasskeyApiRegistrationInitBody body, String idempotencyKey) {
        return t.request("POST", "/v1/passkey_api/registration/init", null, body, idempotencyKey, false, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/passkey_api/registration/finalize} - Verify the browser's attestation and store the passkey for the user. 409 if the credential is already registered; 403 if the authenticator is blocked by the passkey policy. Scope: {@code passkey_api:write}. */
    public PasskeyApiRegistrationFinalizeResponse registrationFinalize(PasskeyApiRegistrationFinalizeBody body) {
        return registrationFinalize(body, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public PasskeyApiRegistrationFinalizeResponse registrationFinalize(PasskeyApiRegistrationFinalizeBody body, String idempotencyKey) {
        return t.request("POST", "/v1/passkey_api/registration/finalize", null, body, idempotencyKey, false, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/passkey_api/login/init} - Start a passkey login. With user_id the user's credentials are listed; without it the login is discoverable and the credential names the user. Scope: {@code passkey_api:write}. */
    public PasskeyApiLoginInitResponse loginInit(PasskeyApiLoginInitBody body) {
        return loginInit(body, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public PasskeyApiLoginInitResponse loginInit(PasskeyApiLoginInitBody body, String idempotencyKey) {
        return t.request("POST", "/v1/passkey_api/login/init", null, body, idempotencyKey, false, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/passkey_api/login/finalize} - Verify the browser's assertion. Returns the user_id the passkey belongs to; your backend then starts its own session. Pass user_id to require that exact user. Challenges are single use. Scope: {@code passkey_api:write}. */
    public PasskeyApiLoginFinalizeResponse loginFinalize(PasskeyApiLoginFinalizeBody body) {
        return loginFinalize(body, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public PasskeyApiLoginFinalizeResponse loginFinalize(PasskeyApiLoginFinalizeBody body, String idempotencyKey) {
        return t.request("POST", "/v1/passkey_api/login/finalize", null, body, idempotencyKey, false, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/passkey_api/users/{userId}/passkeys} - List the passkeys registered for one of your users. Scope: {@code passkey_api:read}. */
    public PasskeyApiListResponse list(String userId) {
        return list(userId, null, null);
    }

    /** As above, with every optional parameter. */
    public PasskeyApiListResponse list(String userId, Long limit, String startingAfter) {
        Map<String, Object> q = Params.of().set("limit", limit).set("starting_after", startingAfter).map();
        return t.request("GET", "/v1/passkey_api/users/" + enc(userId) + "/passkeys", q, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code DELETE /v1/passkey_api/users/{userId}/passkeys/{id}} - Delete one of your user's passkeys. Scope: {@code passkey_api:write}. */
    public PasskeyApiDeleteResponse delete(String userId, String id) {
        return t.request("DELETE", "/v1/passkey_api/users/" + enc(userId) + "/passkeys/" + enc(id), null, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/passkey_api/usage} - Passkeys stored through the Passkey API on this instance (the metered quantity). Scope: {@code passkey_api:read}. */
    public PasskeyApiUsageResponse usage() {
        return t.request("GET", "/v1/passkey_api/usage", null, null, null, false, new TypeReference<>() {
        });
    }
}
