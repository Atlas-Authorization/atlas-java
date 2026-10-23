package net.atlasauth.atlas.betterauth;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * What the bridge decided for one request.
 *
 * <ul>
 *   <li>{@link Action#NONE}: no Better Auth cookie, or an Atlas session is already present.</li>
 *   <li>{@link Action#SIGNED_IN}: set {@link #cookies()} and re-enter (redirect to the same URL).</li>
 *   <li>{@link Action#NEEDS_MFA}: send the browser to your sign-in page carrying the attempt
 *       ({@link #continuationUrl(String)}); {@link #cookies()} clears the spent Better Auth cookies.</li>
 *   <li>{@link Action#REFUSED}: carry on unauthenticated; {@link #cookies()} clears the Better Auth cookies
 *       only when the refusal is final, so a closed bridge, a 429 or an outage retries later.</li>
 * </ul>
 */
public record BridgeDecision(Action action, List<BridgeCookie> cookies, ExchangeResult session, String attemptId,
                             String status, String outcome) {

    public enum Action { NONE, SIGNED_IN, NEEDS_MFA, REFUSED }

    public BridgeDecision {
        cookies = cookies == null ? List.of() : List.copyOf(cookies);
    }

    static BridgeDecision none() {
        return new BridgeDecision(Action.NONE, List.of(), null, "", "", null);
    }

    /** {@code url} with {@code __atlas_attempt} / {@code __atlas_status} set (for {@link Action#NEEDS_MFA}). */
    public String continuationUrl(String url) {
        String fragment = "";
        int hash = url.indexOf('#');
        if (hash >= 0) {
            fragment = url.substring(hash);
            url = url.substring(0, hash);
        }
        String query = "";
        int q = url.indexOf('?');
        if (q >= 0) {
            query = url.substring(q + 1);
            url = url.substring(0, q);
        }
        List<String> kept = new ArrayList<>();
        if (!query.isEmpty()) {
            for (String pair : query.split("&")) {
                String key = URLDecoder.decode(pair.split("=", 2)[0], StandardCharsets.UTF_8);
                if (!key.equals("__atlas_attempt") && !key.equals("__atlas_status")) {
                    kept.add(pair);
                }
            }
        }
        kept.add("__atlas_attempt=" + URLEncoder.encode(attemptId, StandardCharsets.UTF_8));
        kept.add("__atlas_status=" + URLEncoder.encode(status, StandardCharsets.UTF_8));
        return url + "?" + String.join("&", kept) + fragment;
    }

    /** The {@code Set-Cookie} header values for {@link #cookies()}. */
    public List<String> setCookieHeaders() {
        List<String> out = new ArrayList<>();
        for (BridgeCookie c : cookies) {
            out.add(c.toHeader());
        }
        return out;
    }
}
