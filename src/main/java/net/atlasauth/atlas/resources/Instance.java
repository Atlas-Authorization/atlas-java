package net.atlasauth.atlas.resources;

import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import static net.atlasauth.atlas.http.Transport.enc;
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
import net.atlasauth.atlas.models.IntegrationToken;
import net.atlasauth.atlas.models.Preflight;
import net.atlasauth.atlas.models.SetIntegrationBody;
import net.atlasauth.atlas.models.UpdateInstanceBody;
import net.atlasauth.atlas.models.HostedPagesSettings;
import net.atlasauth.atlas.models.SignInWithAtlasSettings;
import net.atlasauth.atlas.models.UpdateHostedPagesBody;
import net.atlasauth.atlas.models.UpdateSignInWithAtlasBody;

/** The {@code /v1/instance} configuration resource. */
public final class Instance {

    private final Transport t;
    private final SignInWithAtlas signInWithAtlas;
    private final HostedPages hostedPages;

    public Instance(Transport t) {
        this.t = t;
        this.signInWithAtlas = new SignInWithAtlas(t);
        this.hostedPages = new HostedPages(t);
    }

    /** "Sign in with Atlas" on the instance ({@code /v1/instance/sign_in_with_atlas}). */
    public SignInWithAtlas signInWithAtlas() {
        return signInWithAtlas;
    }

    /** Where the hosted pages send the browser ({@code /v1/instance/hosted_pages}). */
    public HostedPages hostedPages() {
        return hostedPages;
    }

    public InstanceConfig get() {
        return t.get("/v1/instance", new TypeReference<>() {
        });
    }

    /** {@code PATCH /v1/instance}. {@link InstanceUpdateResult#warnings()} lists what the write did not do as sent. */
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

    /**
     * Write {@code auth_config.accountLinkingTrust}: keys are an exact provider key, a
     * {@code prefix*} or {@code *}; values {@code always} (vouch for the provider's email,
     * for linking only), {@code verified} or {@code never} (refused with 409
     * {@code IDENTITY_ALREADY_LINKED}). An account that never proved its email is never linked.
     */
    public InstanceUpdateResult setAccountLinkingTrust(Map<String, String> trust) {
        return update(new UpdateInstanceBody().authConfig(Map.of("accountLinkingTrust", trust)));
    }

    /**
     * Merge {@code auth_config.captcha}: {@code provider} (see
     * {@link net.atlasauth.atlas.AtlasConstants#CAPTCHA_PROVIDERS}), {@code siteKey},
     * {@code widget}, {@code minScore}, per-flow / per-action overrides and, for Vercel
     * BotID, {@code attestationUrl} (https or a root-relative path).
     */
    public InstanceUpdateResult setCaptchaConfig(Map<String, Object> captcha) {
        return update(new UpdateInstanceBody().authConfig(Map.of("captcha", captcha)));
    }

    /**
     * {@code GET /v1/instance/preflight} with filters. {@code minSeverity} (error, warning, info) keeps only
     * findings at least that severe ({@code ok} still covers every finding); {@code includeSuppressed} also
     * lists the findings that do not apply to how the instance integrates. Both may be null.
     */
    public Preflight preflight(String minSeverity, Boolean includeSuppressed) {
        return t.get("/v1/instance/preflight", Params.of("min_severity", minSeverity).set("include_suppressed", includeSuppressed).map(), new TypeReference<>() {
        });
    }

    /** {@code GET /v1/instance/unknown_fields_report} — Which body fields your callers sent that the endpoints ignored, over the last {@code days} (null = the default). */
    public JsonNode unknownFieldsReport(Integer days) {
        return t.get("/v1/instance/unknown_fields_report", Params.of("days", days).map(), new TypeReference<>() {
        });
    }

    /** {@code GET /v1/openapi/diff} — What changed in the API since a date ({@code since} such as {@code 2026-10-01}); {@code surface} is {@code bapi}, {@code fapi} or {@code platform} (null = all). */
    public JsonNode openapiDiff(String since, String surface) {
        return t.get("/v1/openapi/diff", Params.of("since", since).set("surface", surface).map(), new TypeReference<>() {
        });
    }

    /** {@code GET /v1/integration-health} — Probe every integration the instance depends on. */
    public JsonNode integrationHealth() {
        return integrationHealth(null, null);
    }

    /** {@code GET /v1/integration-health} — {@code only} limits the probe to those checks; {@code probe} false skips the live probes. Both may be null. */
    public JsonNode integrationHealth(List<String> only, Boolean probe) {
        Map<String, Object> q = Params.of("probe", probe).map();
        if (only != null && !only.isEmpty()) {
            q.put("only", String.join(",", only));
        }
        return t.get("/v1/integration-health", q, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/integration-health/providers} — The providers the health check knows. */
    public JsonNode integrationHealthProviders() {
        return t.get("/v1/integration-health/providers", new TypeReference<>() {
        });
    }

    /** {@code GET /v1/embed} — The embed snippet and the CSP / framing requirements for an origin ({@code shape} is {@code button} or {@code widget}). */
    public JsonNode embed(String instance, String origin, String shape) {
        return t.get("/v1/embed", Params.of("instance", instance).set("origin", origin).set("shape", shape).map(), new TypeReference<>() {
        });
    }

    // ---- identity integrations ---------------------------------------------

    /** {@code GET /v1/instance/integrations} — Every integration (the presets, connected or not, and every custom one) with its health. */
    public JsonNode listIntegrations() {
        return t.get("/v1/instance/integrations", new TypeReference<>() {
        });
    }

    /** {@code GET /v1/instance/integrations/{id}} — One integration. */
    public JsonNode getIntegration(String id) {
        return t.get("/v1/instance/integrations/" + enc(id), new TypeReference<>() {
        });
    }

    /**
     * {@code PUT /v1/instance/integrations/{id}} — Connect or replace an identity integration (chatly,
     * forumulate, cookiemunch, referalli or your own). Use {@code atlas.withDryRun()} to validate and save nothing.
     */
    public JsonNode setIntegration(String id, SetIntegrationBody body) {
        return t.put("/v1/instance/integrations/" + enc(id), body, new TypeReference<>() {
        });
    }

    /** {@code DELETE /v1/instance/integrations/{id}} — Disconnect an integration. */
    public JsonNode deleteIntegration(String id) {
        return t.delete("/v1/instance/integrations/" + enc(id), new TypeReference<>() {
        });
    }

    /** {@code GET /v1/instance/integrations/{id}/deliveries} — Event and back-channel logout deliveries to the integration, newest first. {@code trace} finds one operation by trace id; {@code kind} is {@code event} or {@code backchannel_logout}. Each may be null. */
    public JsonNode integrationDeliveries(String id, Integer limit, String trace, String kind) {
        return t.get("/v1/instance/integrations/" + enc(id) + "/deliveries", Params.of("limit", limit).set("trace", trace).set("kind", kind).map(), new TypeReference<>() {
        });
    }

    /** {@code POST /v1/instance/integrations/{id}/events_secret} — Reveal the event receiver's signing secret (audited). */
    public JsonNode revealIntegrationEventsSecret(String id) {
        return t.post("/v1/instance/integrations/" + enc(id) + "/events_secret", null, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/instance/integrations/{id}/atlas_id_tokens} — Mint an integration token for a Sign in with Atlas user from their Atlas ID access token. */
    public IntegrationToken integrationAtlasIdToken(String id, String accessToken) {
        return t.post("/v1/instance/integrations/" + enc(id) + "/atlas_id_tokens", Params.of("access_token", accessToken).map(), new TypeReference<>() {
        });
    }

    /** The {@code integrations} sub-resource. */
    public InstanceIntegrations integrations() {
        return new InstanceIntegrations(t);
    }

    /** {@code /v1/instance/sign_in_with_atlas}. */
    public static final class SignInWithAtlas {
        private final Transport t;

        SignInWithAtlas(Transport t) {
            this.t = t;
        }

        /** {@code GET /v1/instance/sign_in_with_atlas}. */
        public SignInWithAtlasSettings get() {
            return t.get("/v1/instance/sign_in_with_atlas", new TypeReference<>() {
            });
        }

        /** {@code PATCH /v1/instance/sign_in_with_atlas} - {@code enabled(true)} links a connection to the Atlas issuer, else creates one. */
        public SignInWithAtlasSettings update(UpdateSignInWithAtlasBody body) {
            return t.patch("/v1/instance/sign_in_with_atlas", body, new TypeReference<>() {
            });
        }
    }

    /** {@code /v1/instance/hosted_pages}. */
    public static final class HostedPages {
        private final Transport t;

        HostedPages(Transport t) {
            this.t = t;
        }

        /** {@code GET /v1/instance/hosted_pages}. */
        public HostedPagesSettings get() {
            return t.get("/v1/instance/hosted_pages", new TypeReference<>() {
            });
        }

        /** {@code PATCH /v1/instance/hosted_pages} - a set URL makes that hosted page answer 302 there; a cleared one serves the page again. */
        public HostedPagesSettings update(UpdateHostedPagesBody body) {
            return t.patch("/v1/instance/hosted_pages", body, new TypeReference<>() {
            });
        }
    }
}
