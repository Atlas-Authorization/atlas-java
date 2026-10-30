package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;
import java.util.Map;

/**
 * The consent manager, orchestrator, completion webhook and executor key, in one view
 * ({@code GET /v1/instance/privacy_integrations}, {@code POST /v1/instance/privacy_integrations/connect}).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record PrivacyIntegration(
        /** {@code privacy_integration}. */ String object,
        String preset,
        /** Banner on, orchestrator and its completion webhook set, executor key live. */ boolean connected,
        /** The cookie consent settings (with {@code enabled}). */ Map<String, Object> cookieConsent,
        /** The orchestrator connection, as {@code privacy().orchestrator().get()} returns it. */ JsonNode privacyOrchestrator,
        /** The orchestrator's completion webhook. */ JsonNode dsrWebhook,
        /** The executor key, or null. */ ExecutorKey executorKey,
        /** Connect only: the consent-manager site check. */ List<Warning> warnings) {

    /** The executor key: {@code secret} only on the connect that minted it; {@code idempotentReplay} on a re-run. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ExecutorKey(String id, String name, List<String> scopes, String secret, Boolean idempotentReplay) {
        @Override
        public String toString() {
            return "ExecutorKey[id=" + id + ", name=" + name + ", scopes=" + scopes + ", secret=" + (secret == null ? "null" : "***")
                    + ", idempotentReplay=" + idempotentReplay + "]";
        }
    }

    /** A connect warning. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Warning(String code, String message) {
    }
}
