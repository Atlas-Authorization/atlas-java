package net.atlasauth.atlas.webhook;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Verifies an inbound Atlas webhook, for the customer consuming events.
 *
 * <p>Atlas signs every delivery: {@code atlas-signature: v1,<base64>} is HMAC-SHA256 over
 * {@code <id>.<timestamp>.<raw body>} keyed with the endpoint secret ({@code whsec_...}), sent with
 * {@code atlas-id} and {@code atlas-timestamp} (epoch ms). A delivery more than five minutes from
 * now, in either direction, is refused. The header may carry several space-separated
 * {@code v1,<base64>} entries (one per secret while a secret rotates with an overlap); the delivery
 * verifies when any one matches. Verify the RAW body, before any JSON parse. Matches
 * {@code verifyWebhook} in {@code @atlasauth/backend}.
 *
 * <pre>{@code
 * WebhookVerification result = new WebhookVerifier(secret).verify(rawBody, headers);
 * if (!result.verified()) return ResponseEntity.badRequest().build();
 * if (seen.contains(result.deliveryId())) return ResponseEntity.ok().build(); // a retry
 * }</pre>
 */
public final class WebhookVerifier {
    public static final long REPLAY_TOLERANCE_MS = 5 * 60_000L;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final String secret;
    private final long toleranceMs;

    public WebhookVerifier(String secret) {
        this(secret, REPLAY_TOLERANCE_MS);
    }

    public WebhookVerifier(String secret, long toleranceMs) {
        this.secret = secret;
        this.toleranceMs = toleranceMs;
    }

    /** Verify a delivery against the current time. Headers are matched case-insensitively. */
    public WebhookVerification verify(byte[] rawBody, Map<String, ?> headers) {
        return verify(rawBody, headers, System.currentTimeMillis());
    }

    /**
     * Verify a delivery that may carry a v2 signature. An endpoint whose {@code signature_versions} include
     * {@code v2} signs HMAC-SHA256 over {@code <id>.<timestamp>.<url>.<body>}, where {@code url} is the exact
     * URL Atlas POSTed to (the endpoint's {@code url}), so a captured delivery cannot be replayed to another URL.
     * A delivery verifies when any {@code v1} entry (over {@code id.timestamp.body}) or any {@code v2} entry
     * matches; entries of unknown versions are ignored. With {@code requireV2} only a {@code v2} entry is accepted.
     */
    public WebhookVerification verify(byte[] rawBody, Map<String, ?> headers, String endpointUrl, boolean requireV2) {
        return verify(rawBody, headers, System.currentTimeMillis(), endpointUrl, requireV2);
    }

    /** Verify a delivery against {@code nowMs} (epoch ms). */
    public WebhookVerification verify(byte[] rawBody, Map<String, ?> headers, long nowMs) {
        return verify(rawBody, headers, nowMs, null, false);
    }

    /** {@link #verify(byte[], Map, String, boolean)} against {@code nowMs} (epoch ms). */
    public WebhookVerification verify(byte[] rawBody, Map<String, ?> headers, long nowMs, String endpointUrl, boolean requireV2) {
        String id = header(headers, "atlas-id");
        String tsRaw = header(headers, "atlas-timestamp");
        String signature = header(headers, "atlas-signature");
        if (isBlank(id) || isBlank(tsRaw) || isBlank(signature)) {
            return WebhookVerification.failure("missing_headers");
        }
        List<String> provided = requireV2 ? new ArrayList<>() : signatures(signature, "v1");
        List<String> providedV2 = endpointUrl != null ? signatures(signature, "v2") : new ArrayList<>();
        if (provided.isEmpty() && providedV2.isEmpty()) {
            return WebhookVerification.failure("malformed_signature");
        }

        long timestamp;
        try {
            timestamp = Long.parseLong(tsRaw.trim());
        } catch (NumberFormatException e) {
            return WebhookVerification.failure("stale_timestamp");
        }
        if (Math.abs(nowMs - timestamp) > toleranceMs) {
            return WebhookVerification.failure("stale_timestamp");
        }

        String expected = hmac(id + "." + timestamp + ".", rawBody);
        String expectedV2 = endpointUrl != null ? hmac(id + "." + timestamp + "." + endpointUrl + ".", rawBody) : null;
        // Compare every entry (no early exit), each in constant time.
        byte[] want = expected.getBytes(StandardCharsets.UTF_8);
        boolean matched = false;
        for (String candidate : provided) {
            if (MessageDigest.isEqual(want, candidate.getBytes(StandardCharsets.UTF_8))) matched = true;
        }
        if (expectedV2 != null) {
            byte[] wantV2 = expectedV2.getBytes(StandardCharsets.UTF_8);
            for (String candidate : providedV2) {
                if (MessageDigest.isEqual(wantV2, candidate.getBytes(StandardCharsets.UTF_8))) matched = true;
            }
        }
        if (!matched) {
            return WebhookVerification.failure("signature_mismatch");
        }

        JsonNode event = null;
        try {
            JsonNode parsed = MAPPER.readTree(rawBody);
            if (parsed != null && parsed.isObject()) event = parsed;
        } catch (Exception ignored) {
            // A valid signature over a non-JSON body: verified, with no event.
        }
        return new WebhookVerification(true, null, event, id, timestamp);
    }

    /** {@link #verifyStandard(String, byte[], Map, long, long)} against the current time with the default five-minute tolerance. */
    public static WebhookVerification verifyStandard(String secret, byte[] rawBody, Map<String, ?> headers) {
        return verifyStandard(secret, rawBody, headers, System.currentTimeMillis() / 1000, REPLAY_TOLERANCE_MS / 1000);
    }

    /**
     * Verify an APPLICATION webhook (the events an app publishes to its customers' endpoints through
     * {@code appEvents()}): Standard Webhooks. {@code webhook-id}, {@code webhook-timestamp} (decimal epoch
     * SECONDS), {@code webhook-signature: v1,<base64>} (several space-separated entries during a secret rotation;
     * any one matching verifies), HMAC-SHA256 over {@code <id>.<timestamp>.<raw body>} keyed with the
     * base64-decoded part of {@code whsec_<base64>}. Returns the same {@link WebhookVerification} as
     * {@link #verify} ({@code timestamp} in seconds). Matches {@code verifyStandardWebhook} in {@code @atlasauth/backend}.
     *
     * @throws IllegalArgumentException when {@code secret} is not {@code whsec_} followed by base64
     */
    public static WebhookVerification verifyStandard(String secret, byte[] rawBody, Map<String, ?> headers, long nowSec, long toleranceSec) {
        byte[] key = Base64.getDecoder().decode(secret.startsWith("whsec_") ? secret.substring(6) : secret);
        String id = header(headers, "webhook-id");
        String tsRaw = header(headers, "webhook-timestamp");
        String signature = header(headers, "webhook-signature");
        if (isBlank(id) || isBlank(tsRaw) || isBlank(signature)) {
            return WebhookVerification.failure("missing_headers");
        }
        List<String> provided = signatures(signature, "v1");
        if (provided.isEmpty()) {
            return WebhookVerification.failure("malformed_signature");
        }
        if (!tsRaw.matches("\\d{1,18}")) {
            return WebhookVerification.failure("stale_timestamp");
        }
        long timestamp = Long.parseLong(tsRaw);
        if (Math.abs(nowSec - timestamp) > toleranceSec) {
            return WebhookVerification.failure("stale_timestamp");
        }
        byte[] want = hmac(key, id + "." + timestamp + ".", rawBody).getBytes(StandardCharsets.UTF_8);
        boolean matched = false;
        for (String candidate : provided) {
            if (MessageDigest.isEqual(want, candidate.getBytes(StandardCharsets.UTF_8))) matched = true;
        }
        if (!matched) {
            return WebhookVerification.failure("signature_mismatch");
        }
        JsonNode event = null;
        try {
            JsonNode parsed = MAPPER.readTree(rawBody);
            if (parsed != null && parsed.isObject()) event = parsed;
        } catch (Exception ignored) {
            // A valid signature over a non-JSON body: verified, with no event.
        }
        return new WebhookVerification(true, null, event, id, timestamp);
    }

    /**
     * The {@code v1} signatures in an {@code atlas-signature} header: one {@code v1,<base64>}
     * entry, or several separated by spaces (while an endpoint secret rotates, Atlas signs with the
     * old and the new secret). Entries of another version are skipped.
     */
    private static List<String> signatures(String header, String version) {
        List<String> out = new ArrayList<>();
        for (String entry : header.trim().split("\\s+")) {
            int comma = entry.indexOf(',');
            if (comma > 0 && comma < entry.length() - 1 && version.equals(entry.substring(0, comma))) {
                out.add(entry.substring(comma + 1));
            }
        }
        return out;
    }

    private String hmac(String prefix, byte[] rawBody) {
        return hmac(secret.getBytes(StandardCharsets.UTF_8), prefix, rawBody);
    }

    private static String hmac(byte[] key, String prefix, byte[] rawBody) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(key, "HmacSHA256"));
            mac.update(prefix.getBytes(StandardCharsets.UTF_8));
            mac.update(rawBody);
            return Base64.getEncoder().encodeToString(mac.doFinal());
        } catch (Exception e) {
            throw new IllegalStateException("HmacSHA256 is unavailable", e);
        }
    }

    private static String header(Map<String, ?> headers, String name) {
        if (headers == null) return null;
        for (Map.Entry<String, ?> entry : headers.entrySet()) {
            if (entry.getKey() == null || !entry.getKey().equalsIgnoreCase(name)) continue;
            Object value = entry.getValue();
            if (value instanceof List<?> list) value = list.isEmpty() ? null : list.get(0);
            return value == null ? null : value.toString();
        }
        return null;
    }

    private static boolean isBlank(String value) {
        return value == null || value.isEmpty();
    }
}
