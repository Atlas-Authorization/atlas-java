package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.AuthzDecisionsSettingsGetResponse;
import net.atlasauth.atlas.models.AuthzDecisionsSettingsUpdateBody;

import java.util.List;
import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** The {@code /v1/authz_decision_settings} resource. */
public final class AuthzDecisionsSettings {

    private final Transport t;

    public AuthzDecisionsSettings(Transport t) {
        this.t = t;
    }

    /** {@code GET /v1/authz_decision_settings} - How authorization decisions are captured: on or off, sampling per outcome, how much of each request is kept, redaction paths, event shipping and retention. Scope: {@code authz_decisions:read}. */
    public AuthzDecisionsSettingsGetResponse get() {
        return t.request("GET", "/v1/authz_decision_settings", null, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code PUT /v1/authz_decision_settings} - Change how decisions are captured. Fields you omit keep their value. Capture is off by default; `input_mode` defaults to `ids` (identifiers only) so nothing personal is stored until you ask for it. With `ship_to_events` each recorded decision is also an `authz.decision` event for your log streams and webhooks. Scope: {@code authz_decisions:write}. */
    public AuthzDecisionsSettingsGetResponse update(AuthzDecisionsSettingsUpdateBody body) {
        return t.request("PUT", "/v1/authz_decision_settings", null, body, null, false, new TypeReference<>() {
        });
    }
}
