package net.atlasauth.atlas.verify;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A ready-made receiver for OIDC Back-Channel Logout: Atlas POSTs a signed {@code logout_token}
 * (form-encoded) to your client's {@code backchannel_logout_uri} when a session ends (signed out elsewhere,
 * revoked, the user banned or deleted). The handler verifies the token, calls your {@link OnLogout} with whose
 * session to end, and answers the way the spec and Atlas's retries expect:
 *
 * <ul>
 *   <li>{@code 200} verified and handled (also for a replayed token you already handled)</li>
 *   <li>{@code 400} the token is bad: Atlas does not retry</li>
 *   <li>{@code 503} your side could not check it right now (keys unreachable, or {@link OnLogout} threw):
 *       {@code Retry-After} is set and Atlas retries</li>
 * </ul>
 *
 * Every response carries {@code Cache-Control: no-store}. The handler is framework-neutral: pass it the request
 * method and the raw form body, and write the returned {@link Response} with your servlet / Spring / JDK server.
 *
 * <pre>{@code
 * BackchannelLogoutHandler handler = BackchannelLogoutHandler.builder()
 *     .verifier(verifier)
 *     .onLogout(claims -> sessions.endFor(claims.sid(), claims.sub()))
 *     .build();
 * BackchannelLogoutHandler.Response r = handler.handle(request.getMethod(), body);
 * }</pre>
 */
public final class BackchannelLogoutHandler {

    /** Ends the matching session(s) in your app: {@code sid} names one session, {@code sub} the user (end all of them when there is no sid). Throwing answers 503, so Atlas retries. */
    @FunctionalInterface
    public interface OnLogout {
        void handle(LogoutTokenClaims claims) throws Exception;
    }

    /** What to write back: the status, a JSON body and the headers (including {@code Cache-Control: no-store}). */
    public record Response(int status, String body, Map<String, String> headers) {
    }

    private static final int MAX_REMEMBERED = 10_000;

    private final LogoutTokenVerifier verifier;
    private final OnLogout onLogout;
    private final int retryAfterSeconds;
    private final Map<String, Long> handled = new LinkedHashMap<>();

    private BackchannelLogoutHandler(Builder b) {
        if (b.onLogout == null) {
            throw new IllegalArgumentException("BackchannelLogoutHandler requires onLogout.");
        }
        if (b.verifier == null) {
            throw new IllegalArgumentException("BackchannelLogoutHandler requires a verifier.");
        }
        this.verifier = b.verifier;
        this.onLogout = b.onLogout;
        this.retryAfterSeconds = b.retryAfterSeconds > 0 ? b.retryAfterSeconds : 30;
    }

    public static Builder builder() {
        return new Builder();
    }

    /** Handle one request: {@code method} is the HTTP method, {@code formBody} the raw {@code application/x-www-form-urlencoded} body. */
    public Response handle(String method, String formBody) {
        if (!"POST".equalsIgnoreCase(method)) {
            Map<String, String> extra = new LinkedHashMap<>();
            extra.put("Allow", "POST");
            return respond(405, "{\"error\":\"invalid_request\",\"error_description\":\"POST only.\"}", extra);
        }
        String token = LogoutTokenVerifier.extractLogoutToken(formBody);
        if (token == null || token.isEmpty()) {
            return respond(400, "{\"error\":\"invalid_request\",\"error_description\":\"logout_token is missing.\"}", null);
        }
        LogoutTokenClaims claims;
        try {
            claims = verifier.verify(token);
        } catch (LogoutTokenException e) {
            if (e.retryable()) {
                return unavailable(e.getMessage());
            }
            return respond(400, "{\"error\":\"invalid_request\",\"error_description\":" + quote(e.getMessage()) + "}", null);
        } catch (RuntimeException e) {
            return unavailable(String.valueOf(e.getMessage()));
        }
        if (claims.jti() != null && alreadyHandled(claims.jti())) {
            return respond(200, "{\"ok\":true,\"replayed\":true}", null);
        }
        try {
            onLogout.handle(claims);
        } catch (Exception e) {
            return unavailable("could not end the session");
        }
        if (claims.jti() != null) {
            remember(claims.jti());
        }
        return respond(200, "{\"ok\":true}", null);
    }

    private Response unavailable(String description) {
        Map<String, String> extra = new LinkedHashMap<>();
        extra.put("Retry-After", Integer.toString(retryAfterSeconds));
        return respond(503, "{\"error\":\"temporarily_unavailable\",\"error_description\":" + quote(description) + "}", extra);
    }

    private static Response respond(int status, String body, Map<String, String> extra) {
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Cache-Control", "no-store");
        if (extra != null) {
            headers.putAll(extra);
        }
        return new Response(status, body, headers);
    }

    private synchronized boolean alreadyHandled(String jti) {
        return handled.containsKey(jti);
    }

    private synchronized void remember(String jti) {
        handled.put(jti, System.currentTimeMillis());
        if (handled.size() > MAX_REMEMBERED) {
            String eldest = handled.keySet().iterator().next();
            handled.remove(eldest);
        }
    }

    private static String quote(String s) {
        if (s == null) {
            return "\"\"";
        }
        StringBuilder sb = new StringBuilder("\"");
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '"' -> sb.append("\\\"");
                case '\\' -> sb.append("\\\\");
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                case '\t' -> sb.append("\\t");
                default -> {
                    if (c < 0x20) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
                }
            }
        }
        return sb.append('"').toString();
    }

    /** Fluent builder. */
    public static final class Builder {
        private LogoutTokenVerifier verifier;
        private OnLogout onLogout;
        private int retryAfterSeconds;

        /** The verifier to use (keeps its key cache). */
        public Builder verifier(LogoutTokenVerifier verifier) {
            this.verifier = verifier;
            return this;
        }

        /** Ends the matching session(s) in your app. */
        public Builder onLogout(OnLogout onLogout) {
            this.onLogout = onLogout;
            return this;
        }

        /** The {@code Retry-After} of a 503, in seconds (default 30). */
        public Builder retryAfterSeconds(int seconds) {
            this.retryAfterSeconds = seconds;
            return this;
        }

        public BackchannelLogoutHandler build() {
            return new BackchannelLogoutHandler(this);
        }
    }
}
