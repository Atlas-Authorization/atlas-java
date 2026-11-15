package net.atlasauth.atlas.rp;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Jakarta Servlet filter for the hosted-redirect sign-in: it answers {@code /auth/sign-in}, the callback
 * path and {@code /auth/sign-out}, keeps a sealed (AES-GCM, tamper-proof, no server store) session cookie,
 * keeps the Atlas session alive with {@link RelyingParty#touchIfDue}, and exposes the signed-in user to the
 * rest of the app as the request attribute {@value #USER_ATTRIBUTE}.
 *
 * <pre>{@code
 * RelyingPartyFilter filter = RelyingPartyFilter.builder(new RelyingParty(config))
 *     .requireSignIn("/app", "/account")   // anonymous requests there are sent to sign-in and back
 *     .build();
 * // register it: new FilterRegistrationBean<>(filter) in Spring, or <filter> in web.xml.
 * // anywhere downstream:
 * RelyingParty.User user = RelyingPartyFilter.user(request);   // null when anonymous
 * }</pre>
 *
 * <p>The session cookie holds only the user's identity claims and the ID token (for sign-out); it is
 * {@code HttpOnly}, {@code SameSite=Lax} and {@code Secure} on https.
 */
public final class RelyingPartyFilter implements Filter {

    /** The request attribute holding the signed-in {@link RelyingParty.User}, absent when anonymous. */
    public static final String USER_ATTRIBUTE = "atlas.rp.user";

    private final RelyingParty rp;
    private final String signIn;
    private final String callback;
    private final String signOut;
    private final String sessionCookie;
    private final int sessionMaxAgeSeconds;
    private final boolean secure;
    private final List<String> protectedPrefixes;

    private RelyingPartyFilter(Builder b) {
        this.rp = b.rp;
        this.signIn = b.signIn;
        this.callback = b.callback != null ? b.callback : URI.create(b.rp.redirectUri()).getPath();
        this.signOut = b.signOut;
        this.sessionCookie = b.sessionCookie;
        this.sessionMaxAgeSeconds = b.sessionMaxAgeSeconds;
        this.secure = "https".equals(URI.create(b.rp.redirectUri()).getScheme());
        this.protectedPrefixes = List.copyOf(b.protectedPrefixes);
    }

    public static Builder builder(RelyingParty rp) {
        return new Builder(rp);
    }

    /** The signed-in user of this request, or null. */
    public static RelyingParty.User user(ServletRequest request) {
        return request.getAttribute(USER_ATTRIBUTE) instanceof RelyingParty.User u ? u : null;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        String path = request.getRequestURI().substring(request.getContextPath().length());
        String method = request.getMethod();
        Map<String, Object> session = readSession(request);
        try {
            if ("GET".equals(method) && path.equals(signIn)) {
                redirect(response, rp.start(queryOf(request)));
                return;
            }
            if ("GET".equals(method) && path.equals(callback)) {
                handleCallback(request, response);
                return;
            }
            if (path.equals(signOut) && ("GET".equals(method) || "POST".equals(method))) {
                String sid = session == null ? null : (String) session.get("sid");
                String idToken = session == null ? null : (String) session.get("idt");
                RelyingParty.Redirect go = rp.signOut(sid, idToken, null, null, List.of(clearSessionCookie()));
                redirect(response, go);
                return;
            }
        } catch (OidcException e) {
            if (e.clearCookie() != null) {
                response.addHeader("Set-Cookie", e.clearCookie());
            }
            response.setStatus(400);
            response.setContentType("text/plain; charset=utf-8");
            response.setHeader("Cache-Control", "no-store");
            response.getWriter().write("Sign-in failed. Please try again.");
            return;
        }
        if (session != null) {
            session = keepAlive(session, response);
        }
        if (session != null) {
            request.setAttribute(USER_ATTRIBUTE, userOf(session));
        } else if (protectedPrefixes.stream().anyMatch(path::startsWith)) {
            String back = request.getRequestURI() + (request.getQueryString() == null ? "" : "?" + request.getQueryString());
            response.setStatus(302);
            response.setHeader("Location", request.getContextPath() + signIn + (RelyingParty.isSafeReturnTo(back) ? "?return_to=" + URLEncoder.encode(back, StandardCharsets.UTF_8) : ""));
            response.setHeader("Cache-Control", "no-store");
            return;
        }
        chain.doFilter(req, res);
    }

    private void handleCallback(HttpServletRequest request, HttpServletResponse response) throws IOException {
        RelyingParty.CallbackResult done = rp.callback(queryOf(request), request.getHeader("Cookie"));
        RelyingParty.User u = done.user();
        Map<String, Object> s = new LinkedHashMap<>();
        s.put("sub", u.sub());
        s.put("sid", u.sid());
        s.put("email", u.email());
        s.put("ev", u.emailVerified());
        s.put("name", u.name());
        s.put("xid", u.externalId());
        s.put("org", u.orgId());
        s.put("role", u.orgRole());
        s.put("at", u.authTime());
        s.put("idt", done.tokens().idToken());
        s.put("touched", System.currentTimeMillis());
        response.addHeader("Set-Cookie", done.clearCookie());
        response.addHeader("Set-Cookie", sessionCookieHeader(s));
        redirect(response, new RelyingParty.Redirect(done.returnTo() != null ? done.returnTo() : rp.afterSignInPath(), List.of()));
    }

    private Map<String, Object> keepAlive(Map<String, Object> session, HttpServletResponse response) {
        String sid = (String) session.get("sid");
        if (sid == null) {
            return session;
        }
        Long last = session.get("touched") instanceof Number n ? n.longValue() : null;
        RelyingParty.TouchResult t;
        try {
            t = rp.touchIfDue(sid, last);
        } catch (RuntimeException e) {
            return session; // a failing keep-alive never signs the user out
        }
        if ("ended".equals(t.status())) {
            response.addHeader("Set-Cookie", clearSessionCookie());
            return null;
        }
        if ("touched".equals(t.status())) {
            session.put("touched", t.lastTouchedAt());
            response.addHeader("Set-Cookie", sessionCookieHeader(session));
        }
        return session;
    }

    private Map<String, Object> readSession(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie c : cookies) {
            if (c.getName().equals(sessionCookie)) {
                return rp.openSession(c.getValue());
            }
        }
        return null;
    }

    private static RelyingParty.User userOf(Map<String, Object> s) {
        return new RelyingParty.User((String) s.get("sub"), (String) s.get("sid"), (String) s.get("email"), Boolean.TRUE.equals(s.get("ev")),
                (String) s.get("name"), (String) s.get("xid"), (String) s.get("org"), (String) s.get("role"),
                s.get("at") instanceof Number n ? n.longValue() : null);
    }

    private String sessionCookieHeader(Map<String, Object> s) {
        return RelyingParty.cookie(sessionCookie, rp.sealSession(s), "/", sessionMaxAgeSeconds, secure);
    }

    private String clearSessionCookie() {
        return RelyingParty.cookie(sessionCookie, "", "/", 0, secure);
    }

    private static Map<String, String> queryOf(HttpServletRequest request) {
        Map<String, String> q = new LinkedHashMap<>();
        request.getParameterMap().forEach((k, v) -> {
            if (v.length > 0) {
                q.put(k, v[0]);
            }
        });
        return q;
    }

    private static void redirect(HttpServletResponse response, RelyingParty.Redirect go) {
        response.setStatus(302);
        response.setHeader("Location", go.location());
        response.setHeader("Cache-Control", "no-store");
        for (String c : go.cookies()) {
            response.addHeader("Set-Cookie", c);
        }
    }

    public static final class Builder {
        private final RelyingParty rp;
        private String signIn = "/auth/sign-in";
        private String callback;
        private String signOut = "/auth/sign-out";
        private String sessionCookie = "atlas_session";
        private int sessionMaxAgeSeconds = 7 * 24 * 3600;
        private final java.util.ArrayList<String> protectedPrefixes = new java.util.ArrayList<>();

        private Builder(RelyingParty rp) {
            this.rp = rp;
        }

        /** The sign-in path (default {@code /auth/sign-in}). */
        public Builder signInPath(String path) {
            this.signIn = path;
            return this;
        }

        /** The callback path (default: the path of the configured redirect URI). */
        public Builder callbackPath(String path) {
            this.callback = path;
            return this;
        }

        public Builder signOutPath(String path) {
            this.signOut = path;
            return this;
        }

        /** The session cookie name (default {@code atlas_session}). */
        public Builder sessionCookie(String name) {
            this.sessionCookie = name;
            return this;
        }

        public Builder sessionMaxAgeSeconds(int seconds) {
            this.sessionMaxAgeSeconds = seconds;
            return this;
        }

        /** Anonymous requests whose path starts with one of these are redirected to sign-in and back. */
        public Builder requireSignIn(String... pathPrefixes) {
            protectedPrefixes.addAll(List.of(pathPrefixes));
            return this;
        }

        public RelyingPartyFilter build() {
            return new RelyingPartyFilter(this);
        }
    }
}
