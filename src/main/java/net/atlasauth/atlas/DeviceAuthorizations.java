package net.atlasauth.atlas;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Device sign-in approval (OAuth 2.0 Device Authorization Grant, RFC 8628) from your own app:
 * the signed-in user types — or scans — the code a TV, console or CLI shows, and your app
 * approves it instead of sending them to Atlas's hosted {@code /oauth2/device} page.
 *
 * <p>These calls run on the Frontend API as the signed-in USER: they need the user's session
 * token (a native app's session JWT), never a secret key, PAT or OAuth token.
 *
 * <pre>{@code
 * DeviceAuthorizations devices = new DeviceAuthorizations("https://clerk.example.com", sessionToken);
 * Map<String, Object> auth = devices.lookup(typedCode);
 * devices.approve(auth); // or devices.deny(auth)
 * }</pre>
 */
public final class DeviceAuthorizations {

    private static final String BASE = "/v1/client/me/device_authorizations";
    private static final ObjectMapper JSON = new ObjectMapper();

    /** A refused request; {@code code} is the server's stable error code. */
    public static final class DeviceAuthorizationException extends RuntimeException {
        private final int status;
        private final String code;
        private final String param;
        private final String reason;

        public DeviceAuthorizationException(int status, String code, String message, String param, String reason) {
            super(message);
            this.status = status;
            this.code = code;
            this.param = param;
            this.reason = reason;
        }

        public int status() { return status; }
        public String code() { return code; }
        public String param() { return param; }
        public String reason() { return reason; }

        /** Approving needs a fresh re-authentication first. */
        public boolean needsReverification() { return "STEP_UP_REQUIRED".equals(code); }

        /** The code is unknown, expired or already used. */
        public boolean isInvalidCode() { return status == 404 || status == 409; }
    }

    private final String base;
    private final String sessionToken;
    private final String publishableKey;
    private final HttpClient http;

    public DeviceAuthorizations(String frontendApi, String sessionToken) {
        this(frontendApi, sessionToken, null, null);
    }

    /** {@code publishableKey} (sent as {@code x-publishable-key}) and {@code http} may be null. */
    public DeviceAuthorizations(String frontendApi, String sessionToken, String publishableKey, HttpClient http) {
        this.base = frontendApi.replaceAll("/+$", "");
        this.sessionToken = sessionToken;
        this.publishableKey = publishableKey;
        this.http = http != null ? http : HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
    }

    /** Normalise what a person typed into the {@code XXXX-XXXX} form devices show. */
    public static String formatUserCode(String value) {
        String cleaned = value.toUpperCase(Locale.ROOT).replaceAll("[^A-Z0-9]", "");
        return cleaned.length() == 8 ? cleaned.substring(0, 4) + "-" + cleaned.substring(4) : value.trim();
    }

    /** The {@code user_code} of a verification URL the device displayed, formatted; null when none. */
    public static String userCodeFromUrl(String url) {
        try {
            URI u = URI.create(url);
            if (u.getScheme() == null || u.getRawQuery() == null) {
                return null;
            }
            for (String pair : u.getRawQuery().split("&")) {
                int eq = pair.indexOf('=');
                String k = URLDecoder.decode(eq < 0 ? pair : pair.substring(0, eq), StandardCharsets.UTF_8);
                if (k.equals("user_code") && eq >= 0) {
                    String v = URLDecoder.decode(pair.substring(eq + 1), StandardCharsets.UTF_8);
                    return v.isEmpty() ? null : formatUserCode(v);
                }
            }
            return null;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /** Find a pending device sign-in by the code the device shows. */
    public Map<String, Object> lookup(String userCode) {
        return call(BASE + "/lookup", formatUserCode(userCode));
    }

    /** Approve for the signed-in user; the device's next poll receives its tokens. */
    public Map<String, Object> approve(Map<String, Object> authorization) {
        return call(BASE + "/" + URLEncoder.encode(String.valueOf(authorization.get("id")), StandardCharsets.UTF_8)
                + "/approve", String.valueOf(authorization.get("user_code")));
    }

    /** Deny; the device's next poll is told {@code access_denied}. */
    public Map<String, Object> deny(Map<String, Object> authorization) {
        return call(BASE + "/" + URLEncoder.encode(String.valueOf(authorization.get("id")), StandardCharsets.UTF_8)
                + "/deny", String.valueOf(authorization.get("user_code")));
    }

    private Map<String, Object> call(String path, String userCode) {
        String body;
        try {
            body = JSON.writeValueAsString(Map.of("user_code", userCode));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        HttpRequest.Builder req = HttpRequest.newBuilder(URI.create(base + path))
                .timeout(Duration.ofSeconds(10))
                .header("Authorization", "Bearer " + sessionToken)
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body));
        if (publishableKey != null) {
            req.header("x-publishable-key", publishableKey);
        }
        HttpResponse<String> res;
        try {
            res = http.send(req.build(), HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            throw new AtlasConnectionException("Device authorization request failed: " + e.getMessage(), e);
        }
        Map<String, Object> data;
        try {
            data = res.body() == null || res.body().isEmpty() ? Map.of()
                    : JSON.readValue(res.body(), new TypeReference<Map<String, Object>>() {
                    });
        } catch (Exception e) {
            data = Map.of();
        }
        if (res.statusCode() < 200 || res.statusCode() > 299) {
            Map<?, ?> first = data.get("errors") instanceof List<?> errs && !errs.isEmpty() && errs.get(0) instanceof Map<?, ?> m
                    ? m : Map.of();
            throw new DeviceAuthorizationException(res.statusCode(),
                    first.get("code") != null ? String.valueOf(first.get("code")) : "UNKNOWN",
                    first.get("message") != null ? String.valueOf(first.get("message")) : "The request failed.",
                    first.get("param") != null ? String.valueOf(first.get("param")) : null,
                    first.get("reason") != null ? String.valueOf(first.get("reason")) : null);
        }
        return data;
    }
}
