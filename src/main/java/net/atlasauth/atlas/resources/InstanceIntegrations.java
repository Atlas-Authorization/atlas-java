package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.InstanceIntegrationsRotateEventsSecretBody;
import net.atlasauth.atlas.models.InstanceIntegrationsRotateEventsSecretResponse;

import java.util.List;
import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** The {@code /v1/instance} resource. */
public final class InstanceIntegrations {

    private final Transport t;

    public InstanceIntegrations(Transport t) {
        this.t = t;
    }

    /** {@code POST /v1/instance/integrations/{product}/events_secret/rotate} - Rotate the signing secret of the integration's event receiver. The new secret is revealed exactly once. By default (`overlap_seconds` 0) the old secret stops signing at once. With `overlap_seconds` (at most 604800 = 7 days) every delivery carries two signatures in `Atlas-Signature` (`v1,&lt;new&gt; v1,&lt;old&gt;`, the new one first), so the service verifies with either secret while it deploys the new one; after that only the new secret signs. `previous_secret_expires_at` says when the old secret stops signing (epoch ms), or null when it already has. Audited as integration.&lt;id&gt;.events_secret_rotated. 404 when the integration has no event receiver. Scope: {@code instance:write}. */
    public InstanceIntegrationsRotateEventsSecretResponse rotateEventsSecret(String product, InstanceIntegrationsRotateEventsSecretBody body) {
        return rotateEventsSecret(product, body, null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public InstanceIntegrationsRotateEventsSecretResponse rotateEventsSecret(String product, InstanceIntegrationsRotateEventsSecretBody body, String idempotencyKey) {
        return t.request("POST", "/v1/instance/integrations/" + enc(product) + "/events_secret/rotate", null, body, idempotencyKey, false, new TypeReference<>() {
        });
    }
}
