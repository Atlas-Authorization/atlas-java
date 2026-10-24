package net.atlasauth.atlas;

import java.security.SecureRandom;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * W3C trace context helpers. Send a {@code traceparent} on a call ({@link AtlasClient#withTraceparent(String)})
 * and Atlas stores its trace id on the webhook deliveries, back-channel deliveries, referral calls and executor
 * requests the call triggers; read the one Atlas sends to your receiver with {@link #fromHeaders(Map)}.
 */
public final class Traceparent {

    private static final Pattern FORMAT = Pattern.compile("^[0-9a-f]{2}-([0-9a-f]{32})-([0-9a-f]{16})-([0-9a-f]{2})$");
    private static final SecureRandom RANDOM = new SecureRandom();

    private Traceparent() {
    }

    /** A parsed {@code traceparent}. */
    public record Parts(String traceId, String spanId, String flags) {
    }

    /** Mint a fresh sampled {@code traceparent} ({@code 00-<32 hex>-<16 hex>-01}). */
    public static String create() {
        byte[] trace = new byte[16];
        byte[] span = new byte[8];
        RANDOM.nextBytes(trace);
        RANDOM.nextBytes(span);
        // All-zero ids are invalid: nudge the last byte of each.
        if (allZero(trace)) {
            trace[15] = 1;
        }
        if (allZero(span)) {
            span[7] = 1;
        }
        return "00-" + hex(trace) + "-" + hex(span) + "-01";
    }

    /** Parse a {@code traceparent}; null when it is malformed (or has an all-zero trace or span id). */
    public static Parts parse(String traceparent) {
        if (traceparent == null) {
            return null;
        }
        Matcher m = FORMAT.matcher(traceparent.trim().toLowerCase(Locale.ROOT));
        if (!m.matches()) {
            return null;
        }
        if (m.group(1).equals("0".repeat(32)) || m.group(2).equals("0".repeat(16))) {
            return null;
        }
        return new Parts(m.group(1), m.group(2), m.group(3));
    }

    /** The {@code traceparent} among request headers (matched case-insensitively), or null when absent or malformed. */
    public static String fromHeaders(Map<String, ?> headers) {
        if (headers == null) {
            return null;
        }
        for (Map.Entry<String, ?> e : headers.entrySet()) {
            if (e.getKey() == null || !"traceparent".equalsIgnoreCase(e.getKey())) {
                continue;
            }
            Object v = e.getValue();
            if (v instanceof List<?> list) {
                v = list.isEmpty() ? null : list.get(0);
            }
            if (v == null) {
                return null;
            }
            String s = v.toString().trim();
            return parse(s) != null ? s : null;
        }
        return null;
    }

    private static boolean allZero(byte[] b) {
        for (byte x : b) {
            if (x != 0) {
                return false;
            }
        }
        return true;
    }

    private static String hex(byte[] b) {
        StringBuilder sb = new StringBuilder(b.length * 2);
        for (byte x : b) {
            sb.append(Character.forDigit((x >> 4) & 0xF, 16)).append(Character.forDigit(x & 0xF, 16));
        }
        return sb.toString();
    }
}
