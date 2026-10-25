package net.atlasauth.atlas.operator;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.atlasauth.atlas.AtlasClient;
import net.atlasauth.atlas.http.Json;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.LongSupplier;

/**
 * Operator sign-in ({@code atlas login}) and token exchange, without a long-lived key, for CLIs written in Java.
 *
 * <ol>
 *   <li>{@link #start()} runs the RFC 8628 device flow against Atlas's own sign-in with the public atlas-cli
 *       client: show the person {@code verificationUriComplete} / {@code userCode}, then</li>
 *   <li>{@link #poll(DeviceAuthorization)} until it returns the operator access token, then</li>
 *   <li>{@link #exchange(String, String, List)} swaps it (RFC 8693) for a ONE-HOUR Atlas token bound to one
 *       instance ({@code ins_...}, Backend API) or app / account ({@code app_...} / {@code acct_...}, Platform
 *       API), with only the scopes you ask for.</li>
 * </ol>
 *
 * <p>Root actions (create an app or instance, mint a key) need a multi-factor sign-in within 5 minutes;
 * otherwise the API answers 403 {@code STEP_UP_REQUIRED}. A failed step throws {@link OAuthFlowException}.
 */
public final class OperatorLogin {

    /** A pause between polls; replaced in tests. */
    @FunctionalInterface
    public interface Sleeper {
        void sleep(long millis) throws InterruptedException;
    }

    /** {@code GET /v1/operator}: where the device flow and token exchange live. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record OperatorConfiguration(
            String issuer,
            String deviceAuthorizationEndpoint,
            String tokenEndpoint,
            String clientId,
            String scope,
            String tokenExchangeEndpoint,
            String tokenExchangeAudience,
            Integer rootActionMaxMfaAgeSeconds,
            /** Other products that accept Atlas operators (pass one as the audience of a login). */ java.util.List<String> audiences,
            /** The claims an operator access token carries. */ java.util.List<String> accessTokenClaims) {
        /** The shape before audiences and accessTokenClaims. */
        public OperatorConfiguration(String issuer, String deviceAuthorizationEndpoint, String tokenEndpoint, String clientId, String scope, String tokenExchangeEndpoint, String tokenExchangeAudience, Integer rootActionMaxMfaAgeSeconds) {
            this(issuer, deviceAuthorizationEndpoint, tokenEndpoint, clientId, scope, tokenExchangeEndpoint, tokenExchangeAudience, rootActionMaxMfaAgeSeconds, null, null);
        }
    }

    /** The RFC 8628 device authorization response, with the configuration the flow runs against. */
    public record DeviceAuthorization(
            String deviceCode,
            String userCode,
            String verificationUri,
            String verificationUriComplete,
            int expiresIn,
            int interval,
            OperatorConfiguration config) {
    }

    /** A token endpoint answer. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record TokenResponse(
            String accessToken,
            String tokenType,
            int expiresIn,
            String scope,
            String refreshToken,
            String idToken,
            String issuedTokenType,
            /** The resource a token exchange bound the token to. */ String resource) {
    }

    /** One outcome of {@link #revokeLeaked}: {@code revoked}, {@code already_revoked} or {@code not_found}. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record LeakedKeyResult(int index, String kind, String outcome) {
    }

    /** A failed device-flow, token-exchange or token request. {@link #code()} is the OAuth error code (access_denied, expired_token, invalid_grant, STEP_UP_REQUIRED, ...). */
    public static final class OAuthFlowException extends RuntimeException {
        private final String code;
        private final String description;
        private final int status;

        public OAuthFlowException(String code, String description, int status) {
            super(description != null && !description.isEmpty()
                    ? code + ": " + description + " (HTTP " + status + ")"
                    : code + " (HTTP " + status + ")");
            this.code = code;
            this.description = description;
            this.status = status;
        }

        public String code() { return code; }
        public String description() { return description; }
        public int status() { return status; }
    }

    public static final String DEFAULT_API_URL = AtlasClient.DEFAULT_API_URL;

    private static final String DEVICE_GRANT = "urn:ietf:params:oauth:grant-type:device_code";
    private static final String EXCHANGE_GRANT = "urn:ietf:params:oauth:grant-type:token-exchange";
    private static final String ACCESS_TOKEN_TYPE = "urn:ietf:params:oauth:token-type:access_token";

    private final String apiUrl;
    private final HttpClient http;
    private final ObjectMapper mapper = Json.mapper();
    private final Sleeper sleeper;
    private final LongSupplier clock;

    /** Against the production API. */
    public OperatorLogin() {
        this(DEFAULT_API_URL);
    }

    /** Against {@code apiUrl}. */
    public OperatorLogin(String apiUrl) {
        this(apiUrl, HttpClient.newHttpClient(), Thread::sleep, System::currentTimeMillis);
    }

    /** With an injected HTTP client, sleeper and clock (tests). */
    public OperatorLogin(String apiUrl, HttpClient http, Sleeper sleeper, LongSupplier clock) {
        this.apiUrl = apiUrl.replaceAll("/+$", "");
        this.http = http;
        this.sleeper = sleeper;
        this.clock = clock;
    }

    /** {@code GET /v1/operator} (no key needed). */
    public OperatorConfiguration getConfiguration() {
        HttpRequest req = HttpRequest.newBuilder(URI.create(apiUrl + "/v1/operator"))
                .timeout(Duration.ofSeconds(30)).header("accept", "application/json").GET().build();
        HttpResponse<String> res = send(req);
        if (res.statusCode() < 200 || res.statusCode() >= 300) {
            throw new OAuthFlowException("not_configured", "The operator issuer is not configured on this deployment.", res.statusCode());
        }
        try {
            return mapper.readValue(res.body(), OperatorConfiguration.class);
        } catch (IOException e) {
            throw new OAuthFlowException("invalid_response", "Could not read the operator configuration.", res.statusCode());
        }
    }

    /** Begin the device flow, reading the configuration from {@code GET /v1/operator}. */
    public DeviceAuthorization start() {
        return start(getConfiguration());
    }

    /** Begin the device flow against a known configuration. */
    public DeviceAuthorization start(OperatorConfiguration config) {
        Map<String, String> form = new LinkedHashMap<>();
        form.put("client_id", config.clientId());
        form.put("scope", config.scope());
        Result r = postForm(config.deviceAuthorizationEndpoint(), form);
        if (r.status != 200) {
            throw flowError(r, "Device authorization failed.");
        }
        return new DeviceAuthorization(text(r.body, "device_code"), text(r.body, "user_code"), text(r.body, "verification_uri"),
                text(r.body, "verification_uri_complete"), r.body.path("expires_in").asInt(0), r.body.path("interval").asInt(0), config);
    }

    /**
     * Poll until the person approves (returning the operator token), denies or the code expires (an
     * {@link OAuthFlowException}). Honours the interval and {@code slow_down}.
     */
    public TokenResponse poll(DeviceAuthorization start) {
        long waitMs = Math.max(1, start.interval()) * 1000L;
        long deadline = clock.getAsLong() + start.expiresIn() * 1000L;
        while (clock.getAsLong() < deadline) {
            try {
                sleeper.sleep(waitMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new OAuthFlowException("interrupted", "The sign-in was interrupted.", 0);
            }
            Map<String, String> form = new LinkedHashMap<>();
            form.put("grant_type", DEVICE_GRANT);
            form.put("device_code", start.deviceCode());
            form.put("client_id", start.config().clientId());
            Result r = postForm(start.config().tokenEndpoint(), form);
            if (r.status == 200) {
                return token(r);
            }
            String code = text(r.body, "error");
            if ("authorization_pending".equals(code)) {
                continue;
            }
            if ("slow_down".equals(code)) {
                waitMs += 5000L;
                continue;
            }
            throw flowError(r, "Sign-in failed.");
        }
        throw new OAuthFlowException("expired_token", "The sign-in code expired. Run the login again.", 400);
    }

    /**
     * Swap an operator token (RFC 8693) for a one-hour, resource-bound Atlas token. {@code resource} is an
     * {@code ins_...} / {@code app_...} / {@code acct_...} id; {@code scopes} are the scopes to keep.
     */
    public TokenResponse exchange(String subjectToken, String resource, List<String> scopes) {
        Map<String, String> form = new LinkedHashMap<>();
        form.put("grant_type", EXCHANGE_GRANT);
        form.put("subject_token", subjectToken);
        form.put("subject_token_type", ACCESS_TOKEN_TYPE);
        form.put("audience", "atlas");
        form.put("resource", resource);
        form.put("scope", scopes == null ? "" : String.join(" ", scopes));
        Result r = postForm(apiUrl + "/v1/oauth/token", form);
        if (r.status != 200) {
            throw flowError(r, "Token exchange failed.");
        }
        return token(r);
    }

    /** {@code client_credentials} with a partner link's credentials (a partner service calling an instance). {@code scope} may be null. */
    public TokenResponse partnerLinkToken(String clientId, String clientSecret, String scope) {
        Map<String, String> form = new LinkedHashMap<>();
        form.put("grant_type", "client_credentials");
        form.put("client_id", clientId);
        form.put("client_secret", clientSecret);
        if (scope != null && !scope.isEmpty()) {
            form.put("scope", scope);
        }
        Result r = postForm(apiUrl + "/v1/oauth/token", form);
        if (r.status != 200) {
            throw flowError(r, "Token request failed.");
        }
        return token(r);
    }

    /** {@code POST /v1/keys/revoke_leaked} (no key needed): revoke leaked Atlas credentials. {@code sourceUrl} optionally says where the secret was found. */
    public List<LeakedKeyResult> revokeLeaked(List<String> secrets, String sourceUrl) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("secrets", secrets);
        if (sourceUrl != null && !sourceUrl.isEmpty()) {
            payload.put("url", sourceUrl);
        }
        String json;
        try {
            json = mapper.writeValueAsString(payload);
        } catch (IOException e) {
            throw new IllegalArgumentException("Could not encode the request", e);
        }
        HttpRequest req = HttpRequest.newBuilder(URI.create(apiUrl + "/v1/keys/revoke_leaked"))
                .timeout(Duration.ofSeconds(30)).header("content-type", "application/json").header("accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8)).build();
        HttpResponse<String> res = send(req);
        if (res.statusCode() < 200 || res.statusCode() >= 300) {
            throw new OAuthFlowException("request_failed", "revoke_leaked failed (" + res.statusCode() + ").", res.statusCode());
        }
        try {
            JsonNode results = mapper.readTree(res.body()).path("results");
            return mapper.convertValue(results, new TypeReference<List<LeakedKeyResult>>() {
            });
        } catch (IOException e) {
            throw new OAuthFlowException("invalid_response", "Could not read the revoke_leaked answer.", res.statusCode());
        }
    }

    /**
     * An {@link AtlasClient} signed with a one-hour Atlas access token from {@link #exchange}. An {@code ins_...}
     * token drives the Backend API; an {@code app_...} / {@code acct_...} token drives {@link AtlasClient#platform()}.
     */
    public static AtlasClient clientFromAccessToken(String token, String apiUrl) {
        if (token == null || token.isEmpty()) {
            throw new IllegalArgumentException("empty access token");
        }
        return AtlasClient.builder().secretKey(token).platformKey(token).apiUrl(apiUrl != null ? apiUrl : DEFAULT_API_URL).build();
    }

    // ---- internals ----------------------------------------------------------

    private record Result(int status, JsonNode body) {
    }

    private Result postForm(String endpoint, Map<String, String> form) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> e : form.entrySet()) {
            if (sb.length() > 0) {
                sb.append('&');
            }
            sb.append(URLEncoder.encode(e.getKey(), StandardCharsets.UTF_8)).append('=')
                    .append(URLEncoder.encode(e.getValue() == null ? "" : e.getValue(), StandardCharsets.UTF_8));
        }
        HttpRequest req = HttpRequest.newBuilder(URI.create(endpoint)).timeout(Duration.ofSeconds(30))
                .header("content-type", "application/x-www-form-urlencoded").header("accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(sb.toString(), StandardCharsets.UTF_8)).build();
        HttpResponse<String> res = send(req);
        JsonNode body;
        try {
            body = res.body() == null || res.body().isEmpty() ? mapper.createObjectNode() : mapper.readTree(res.body());
            if (body == null || !body.isObject()) {
                body = mapper.createObjectNode();
            }
        } catch (IOException e) {
            body = mapper.createObjectNode();
        }
        return new Result(res.statusCode(), body);
    }

    private HttpResponse<String> send(HttpRequest req) {
        try {
            return http.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new OAuthFlowException("network_error", "Request to " + req.uri() + " failed: " + e.getMessage(), 0);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new OAuthFlowException("interrupted", "The request was interrupted.", 0);
        }
    }

    private TokenResponse token(Result r) {
        try {
            return mapper.treeToValue(r.body, TokenResponse.class);
        } catch (IOException e) {
            throw new OAuthFlowException("invalid_response", "Could not read the token response.", r.status);
        }
    }

    private static String text(JsonNode node, String field) {
        JsonNode v = node.get(field);
        return v == null || v.isNull() ? null : v.asText();
    }

    /** Accepts both the OAuth error shape and the Atlas error envelope. */
    private static OAuthFlowException flowError(Result r, String fallback) {
        String code = text(r.body, "error");
        String desc = text(r.body, "error_description");
        if (code == null || code.isEmpty()) {
            JsonNode errs = r.body.get("errors");
            if (errs != null && errs.isArray() && errs.size() > 0) {
                JsonNode first = errs.get(0);
                code = text(first, "code");
                if (desc == null || desc.isEmpty()) {
                    desc = text(first, "message");
                }
            }
        }
        if (code == null || code.isEmpty()) {
            code = "error";
        }
        if (desc == null || desc.isEmpty()) {
            desc = fallback;
        }
        return new OAuthFlowException(code, desc, r.status);
    }
}
