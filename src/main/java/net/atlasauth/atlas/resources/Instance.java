package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.CookieConsent;
import net.atlasauth.atlas.models.ChatlyIntegration;
import net.atlasauth.atlas.models.CookieConsentBody;
import net.atlasauth.atlas.models.ForumulateIntegration;
import net.atlasauth.atlas.models.ForumulateIntegrationBody;
import net.atlasauth.atlas.models.InstanceConnections;
import net.atlasauth.atlas.models.OriginMove;
import net.atlasauth.atlas.models.OriginMoveBody;
import net.atlasauth.atlas.models.InstanceConfig;
import net.atlasauth.atlas.models.InstanceUpdateResult;
import net.atlasauth.atlas.models.Preflight;
import net.atlasauth.atlas.models.UpdateInstanceBody;

/** The {@code /v1/instance} configuration resource. */
public final class Instance {

    private final Transport t;

    public Instance(Transport t) {
        this.t = t;
    }

    public InstanceConfig get() {
        return t.get("/v1/instance", new TypeReference<>() {
        });
    }

    public InstanceUpdateResult update(UpdateInstanceBody body) {
        return t.patch("/v1/instance", body, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/instance/preflight} — Check your setup in one call. */
    public Preflight preflight() {
        return t.get("/v1/instance/preflight", new TypeReference<>() {
        });
    }

    /** {@code POST /v1/instance/move_origin} — Move every URL that points at one origin to another (a domain move), across the instance, its OAuth clients, webhooks, redirect URLs and Sign in with Atlas apps. {@code dryRun(true)} writes nothing. */
    public OriginMove moveOrigin(OriginMoveBody body) {
        return t.post("/v1/instance/move_origin", body, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/instance/connections} — The instance's links to other platforms and their health. Read-only. */
    public InstanceConnections connections() {
        return t.get("/v1/instance/connections", new TypeReference<>() {
        });
    }

    /** {@code GET /v1/instance/integrations/chatly} — The Chatly connection (enabled:false when none). */
    public ChatlyIntegration getChatlyIntegration() {
        return t.get("/v1/instance/integrations/chatly", new TypeReference<>() {
        });
    }

    /** {@code PUT /v1/instance/integrations/chatly} — Connect a Chatly channel (its public id). */
    public ChatlyIntegration setChatlyIntegration(String channel) {
        return t.put("/v1/instance/integrations/chatly", java.util.Map.of("channel", channel), new TypeReference<>() {
        });
    }

    /** {@code DELETE /v1/instance/integrations/chatly} — Remove the Chatly connection. */
    public ChatlyIntegration deleteChatlyIntegration() {
        return t.delete("/v1/instance/integrations/chatly", new TypeReference<>() {
        });
    }

    /** {@code GET /v1/instance/integrations/forumulate} — The Forumulate connection (enabled:false when none). */
    public ForumulateIntegration getForumulateIntegration() {
        return t.get("/v1/instance/integrations/forumulate", new TypeReference<>() {
        });
    }

    /** {@code PUT /v1/instance/integrations/forumulate} — Connect Forumulate spaces (replaces the whole connection). */
    public ForumulateIntegration setForumulateIntegration(ForumulateIntegrationBody body) {
        return t.put("/v1/instance/integrations/forumulate", body, new TypeReference<>() {
        });
    }

    /** {@code DELETE /v1/instance/integrations/forumulate} — Remove the Forumulate connection. */
    public ForumulateIntegration deleteForumulateIntegration() {
        return t.delete("/v1/instance/integrations/forumulate", new TypeReference<>() {
        });
    }

    /** {@code GET /v1/instance/cookie_consent} — Read the cookie-consent CMP injected into the hosted sign-in, sign-up and account pages (enabled:false when none). */
    public CookieConsent getCookieConsent() {
        return t.get("/v1/instance/cookie_consent", new TypeReference<>() {
        });
    }

    /** {@code PUT /v1/instance/cookie_consent} — Set the cookie-consent CMP for the hosted pages (any provider). */
    public CookieConsent replaceCookieConsent(CookieConsentBody body) {
        return t.put("/v1/instance/cookie_consent", body, new TypeReference<>() {
        });
    }

    /** {@code DELETE /v1/instance/cookie_consent} — Turn off the hosted-page cookie-consent CMP: no script is injected and the CSP goes back to normal. */
    public CookieConsent deleteCookieConsent() {
        return t.delete("/v1/instance/cookie_consent", new TypeReference<>() {
        });
    }
}
