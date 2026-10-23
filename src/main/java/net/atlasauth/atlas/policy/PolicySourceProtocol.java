package net.atlasauth.atlas.policy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

/**
 * Atlas policy source protocol v1: the helpers for the system that SERVES an instance's policy
 * documents (a CMS, a docs platform, your own service).
 *
 * <ul>
 *   <li>Atlas to you: {@code GET {endpoint_url}/policies} (the full feed) or
 *       {@code GET {endpoint_url}/policies?key=<key>} (one policy, after your event), signed with
 *       the source's signing secret: {@code atlas-id}, {@code atlas-timestamp} (epoch
 *       milliseconds) and {@code atlas-signature: v1,<base64 HMAC-SHA256>} over
 *       {@code {atlas-id}.{atlas-timestamp}.GET\n{path+query}}. Verify it with
 *       {@link #verifyFeedRequest}, then answer a feed
 *       ({@code {"object":"policy_list","policies":[...]}}, at most 512 KB per version).</li>
 *   <li>You to Atlas: {@code POST {webhook_url}} (from {@code policySource().get()}), an event
 *       signed with the same secret: {@link #signEvent} (Atlas-style headers) or any Standard
 *       Webhooks library. Atlas then re-fetches that policy; it never stores content from the event.</li>
 * </ul>
 */
public final class PolicySourceProtocol {

    public static final String PROTOCOL_VERSION = "2026-10-24";
    public static final String VERSION_HEADER = "atlas-policies-version";
    public static final long TOLERANCE_MS = 5 * 60_000L;

    /** Webhook event type: a version was published. */
    public static final String EVENT_POLICY_PUBLISHED = "policy.published";
    /** Webhook event type: a policy was retired. */
    public static final String EVENT_POLICY_RETIRED = "policy.retired";

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private PolicySourceProtocol() {
    }

    /** Why a feed request was refused. */
    public enum Failure {
        MISSING_HEADERS, MALFORMED_SIGNATURE, STALE_TIMESTAMP, SIGNATURE_MISMATCH, WRONG_METHOD
    }

    /**
     * The outcome of {@link #verifyFeedRequest}. When verified, {@code key} is the {@code key} query
     * parameter (null for the full feed) and {@code protocolVersion} the {@code atlas-policies-version}
     * header; otherwise {@code failure} says why.
     */
    public record FeedVerifyResult(boolean verified, Failure failure, String deliveryId, Long timestamp, String key,
                                   String protocolVersion) {
    }

    /** A signed event: send {@code body} byte-for-byte with {@code headers}. */
    public record SignedEvent(String body, Map<String, String> headers) {
    }

    /**
     * Verify Atlas's signed GET of your feed with the current time and the 5-minute tolerance.
     * Pass the request's path AND query exactly as received (for example the servlet's
     * {@code getRequestURI()} plus {@code ?} plus {@code getQueryString()}), and the method.
     * {@code headers} looks a header up by lower-case name (null when absent).
     */
    public static FeedVerifyResult verifyFeedRequest(String method, String pathAndQuery,
                                                     Function<String, String> headers, String secret) {
        return verifyFeedRequest(method, pathAndQuery, headers, secret, System.currentTimeMillis(), TOLERANCE_MS);
    }

    /** {@link #verifyFeedRequest(String, String, Function, String)} with an explicit clock and tolerance. */
    public static FeedVerifyResult verifyFeedRequest(String method, String pathAndQuery,
                                                     Function<String, String> headers, String secret,
                                                     long nowMs, long toleranceMs) {
        if (method != null && !method.equalsIgnoreCase("GET")) {
            return fail(Failure.WRONG_METHOD);
        }
        String id = headers.apply("atlas-id");
        String ts = headers.apply("atlas-timestamp");
        String sig = headers.apply("atlas-signature");
        if (blank(id) || blank(ts) || blank(sig)) {
            return fail(Failure.MISSING_HEADERS);
        }
        java.util.List<byte[]> provided = new java.util.ArrayList<>();
        for (String entry : sig.trim().split("\\s+")) {
            if (entry.startsWith("v1,")) {
                try {
                    provided.add(Base64.getDecoder().decode(entry.substring(3)));
                } catch (IllegalArgumentException e) {
                    provided.add(new byte[0]);
                }
            }
        }
        if (provided.isEmpty()) {
            return fail(Failure.MALFORMED_SIGNATURE);
        }
        long timestamp;
        try {
            timestamp = Long.parseLong(ts.trim());
        } catch (NumberFormatException e) {
            return fail(Failure.STALE_TIMESTAMP);
        }
        if (Math.abs(nowMs - timestamp) > toleranceMs) {
            return fail(Failure.STALE_TIMESTAMP);
        }
        byte[] expected = mac(secret, id, ts, "GET\n" + pathAndQuery);
        boolean ok = false;
        for (byte[] p : provided) {
            // evaluate every candidate: no early exit on the first match
            ok |= MessageDigest.isEqual(p, expected);
        }
        if (!ok) {
            return fail(Failure.SIGNATURE_MISMATCH);
        }
        return new FeedVerifyResult(true, null, id, timestamp, queryParam(pathAndQuery, "key"), headers.apply(VERSION_HEADER));
    }

    /** Sign a {@code policy.published} event for {@code POST {webhook_url}}. */
    public static SignedEvent signPublished(String key, String version, String secret) {
        return signEvent(EVENT_POLICY_PUBLISHED, key, version, secret, null, null);
    }

    /** Sign a {@code policy.retired} event for {@code POST {webhook_url}}. */
    public static SignedEvent signRetired(String key, String secret) {
        return signEvent(EVENT_POLICY_RETIRED, key, null, secret, null, null);
    }

    /**
     * Sign an event (Atlas-style headers). {@code type} is {@link #EVENT_POLICY_PUBLISHED} (needs
     * {@code version}) or {@link #EVENT_POLICY_RETIRED}. {@code id} (default {@code evt_<uuid>}) and
     * {@code timestampMs} (default now) may be null.
     */
    public static SignedEvent signEvent(String type, String key, String version, String secret, String id, Long timestampMs) {
        if (!EVENT_POLICY_PUBLISHED.equals(type) && !EVENT_POLICY_RETIRED.equals(type)) {
            throw new IllegalArgumentException("Unknown policy source event type: " + type);
        }
        if (EVENT_POLICY_PUBLISHED.equals(type) && blank(version)) {
            throw new IllegalArgumentException("A policy.published event needs a version.");
        }
        Map<String, Object> event = new LinkedHashMap<>();
        event.put("occurred_at", Instant.now().toString());
        event.put("type", type);
        event.put("key", key);
        if (EVENT_POLICY_PUBLISHED.equals(type)) {
            event.put("version", version);
        }
        return signBody(json(event), secret, id, timestampMs);
    }

    /** Sign an already serialised JSON body. */
    public static SignedEvent signBody(String body, String secret, String id, Long timestampMs) {
        String eventId = id != null ? id : "evt_" + UUID.randomUUID();
        String ts = String.valueOf(timestampMs != null ? timestampMs : System.currentTimeMillis());
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("atlas-id", eventId);
        headers.put("atlas-timestamp", ts);
        headers.put("atlas-signature", "v1," + Base64.getEncoder().encodeToString(mac(secret, eventId, ts, body)));
        return new SignedEvent(body, headers);
    }

    private static FeedVerifyResult fail(Failure f) {
        return new FeedVerifyResult(false, f, null, null, null, null);
    }

    private static boolean blank(String s) {
        return s == null || s.isEmpty();
    }

    private static String json(Object o) {
        try {
            return MAPPER.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }

    private static String queryParam(String pathAndQuery, String name) {
        int q = pathAndQuery.indexOf('?');
        if (q == -1) {
            return null;
        }
        for (String pair : pathAndQuery.substring(q + 1).split("&")) {
            int eq = pair.indexOf('=');
            String k = URLDecoder.decode(eq == -1 ? pair : pair.substring(0, eq), StandardCharsets.UTF_8);
            if (k.equals(name)) {
                return eq == -1 ? "" : URLDecoder.decode(pair.substring(eq + 1), StandardCharsets.UTF_8);
            }
        }
        return null;
    }

    private static byte[] mac(String secret, String id, String timestamp, String payload) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return mac.doFinal((id + "." + timestamp + "." + payload).getBytes(StandardCharsets.UTF_8));
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException(e);
        }
    }
}
