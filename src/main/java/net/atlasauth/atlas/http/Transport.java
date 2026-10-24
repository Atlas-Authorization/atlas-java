package net.atlasauth.atlas.http;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.atlasauth.atlas.AtlasApiException;
import net.atlasauth.atlas.AtlasConnectionException;
import net.atlasauth.atlas.ErrorItem;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * The shared HTTP core every resource calls. One place decides how a Backend API
 * request is authenticated, serialized, and how a failure becomes an
 * {@link AtlasApiException} — so a resource method stays a one-liner naming a
 * method, a path, and its result type.
 *
 * <p>Built on the JDK's {@link HttpClient}, so the SDK drags in no third-party
 * HTTP stack. The secret key is sent only as {@code Authorization: Bearer <key>}
 * and is never logged nor placed in a URL.
 */
public final class Transport {

    /** The request header that turns unknown body fields into a {@code 422 UNKNOWN_FIELD}. */
    public static final String STRICT_HEADER = "Atlas-Strict";
    /** The response header listing body fields the server ignored. */
    public static final String UNKNOWN_FIELDS_HEADER = "Atlas-Unknown-Fields";

    private static final System.Logger LOG = System.getLogger("net.atlasauth.atlas");

    private final String secretKey;
    private final String baseUrl;
    private final HttpClient http;
    private final ObjectMapper mapper;
    private final Duration timeout;
    private final Boolean strict;
    private final Consumer<UnknownFieldsWarning> onUnknownFields;
    private final String missingKeyMessage;
    private final boolean dryRun;
    private final String traceparent;
    private final String secretRecipient;

    public Transport(String secretKey, String baseUrl, HttpClient http, ObjectMapper mapper, Duration timeout) {
        this(secretKey, baseUrl, http, mapper, timeout, null, null, null);
    }

    /**
     * @param strict          {@code true}/{@code false} sends {@code Atlas-Strict} on every write;
     *                        {@code null} sends nothing (the instance default applies)
     * @param onUnknownFields called when a response carries {@code Atlas-Unknown-Fields};
     *                        {@code null} logs a warning through {@link System.Logger}
     * @param missingKeyMessage the error raised when {@code secretKey} is absent
     */
    public Transport(String secretKey, String baseUrl, HttpClient http, ObjectMapper mapper, Duration timeout,
                     Boolean strict, Consumer<UnknownFieldsWarning> onUnknownFields, String missingKeyMessage) {
        this(secretKey, baseUrl, http, mapper, timeout, strict, onUnknownFields, missingKeyMessage, false, null);
    }

    /**
     * @param dryRun      {@code true} sends every write (POST, PUT, PATCH, DELETE) with {@code ?dry_run=true}
     *                    and without an {@code Idempotency-Key}: Atlas validates it and answers with the
     *                    changes it would make, changing nothing
     * @param traceparent a W3C {@code traceparent} sent on every call ({@code null} = none)
     */
    public Transport(String secretKey, String baseUrl, HttpClient http, ObjectMapper mapper, Duration timeout,
                     Boolean strict, Consumer<UnknownFieldsWarning> onUnknownFields, String missingKeyMessage,
                     boolean dryRun, String traceparent) {
        this(secretKey, baseUrl, http, mapper, timeout, strict, onUnknownFields, missingKeyMessage, dryRun, traceparent, null);
    }

    /**
     * @param secretRecipient an {@code x25519:<base64 public key>} sent as {@code X-Secret-Recipient} on every call, so
     *                        every shown-once secret in an answer comes back sealed ({@code null} = none)
     */
    public Transport(String secretKey, String baseUrl, HttpClient http, ObjectMapper mapper, Duration timeout,
                     Boolean strict, Consumer<UnknownFieldsWarning> onUnknownFields, String missingKeyMessage,
                     boolean dryRun, String traceparent, String secretRecipient) {
        this.secretRecipient = secretRecipient;
        this.dryRun = dryRun;
        this.traceparent = traceparent;
        this.secretKey = secretKey;
        this.baseUrl = baseUrl.replaceAll("/+$", "");
        this.http = http;
        this.mapper = mapper;
        this.timeout = timeout;
        this.strict = strict;
        this.onUnknownFields = onUnknownFields;
        this.missingKeyMessage = missingKeyMessage;
    }

    public ObjectMapper mapper() {
        return mapper;
    }

    /** The strict-body setting this transport sends ({@code null} = header not sent). */
    public Boolean strict() {
        return strict;
    }

    /** A copy of this transport that sends {@code Atlas-Strict: <strict>} on writes ({@code null} = never). */
    public Transport withStrict(Boolean strict) {
        return new Transport(secretKey, baseUrl, http, mapper, timeout, strict, onUnknownFields, missingKeyMessage, dryRun, traceparent, secretRecipient);
    }

    /** A copy of this transport that sends every write as a dry run ({@code ?dry_run=true}). */
    public Transport withDryRun(boolean dryRun) {
        return new Transport(secretKey, baseUrl, http, mapper, timeout, strict, onUnknownFields, missingKeyMessage, dryRun, traceparent, secretRecipient);
    }

    /** A copy of this transport that sends a W3C {@code traceparent} on every call ({@code null} = none). */
    public Transport withTraceparent(String traceparent) {
        return new Transport(secretKey, baseUrl, http, mapper, timeout, strict, onUnknownFields, missingKeyMessage, dryRun, traceparent, secretRecipient);
    }

    /** A copy of this transport that sends {@code X-Secret-Recipient} on every call ({@code null} = none). */
    public Transport withSecretRecipient(String secretRecipient) {
        return new Transport(secretKey, baseUrl, http, mapper, timeout, strict, onUnknownFields, missingKeyMessage, dryRun, traceparent, secretRecipient);
    }

    // ---- Convenience verbs -------------------------------------------------

    public <T> T get(String path, TypeReference<T> type) {
        return request("GET", path, null, null, null, false, type);
    }

    public <T> T get(String path, Map<String, Object> query, TypeReference<T> type) {
        return request("GET", path, query, null, null, false, type);
    }

    public <T> T post(String path, Object body, TypeReference<T> type) {
        return request("POST", path, null, body, null, false, type);
    }

    public <T> T post(String path, Object body, String idempotencyKey, TypeReference<T> type) {
        return request("POST", path, null, body, idempotencyKey, false, type);
    }

    public <T> T patch(String path, Object body, TypeReference<T> type) {
        return request("PATCH", path, null, body, null, false, type);
    }

    public <T> T put(String path, Object body, TypeReference<T> type) {
        return request("PUT", path, null, body, null, false, type);
    }

    public <T> T delete(String path, TypeReference<T> type) {
        return request("DELETE", path, null, null, null, false, type);
    }

    public <T> T delete(String path, Map<String, Object> query, TypeReference<T> type) {
        return request("DELETE", path, query, null, null, false, type);
    }

    /** Return a raw text body (e.g. SAML metadata XML) rather than parsed JSON. */
    public String getRaw(String path) {
        return getRaw(path, null);
    }

    /** Return a raw text body (e.g. an NDJSON or CSV export) for a GET with a query. */
    public String getRaw(String path, Map<String, Object> query) {
        return request("GET", path, query, null, null, true, new TypeReference<String>() {
        });
    }

    /**
     * Send raw bytes (an image upload) with the given {@code Content-Type} instead
     * of a JSON body; the response is decoded as JSON like any other call.
     */
    public <T> T requestBytes(String method, String path, Map<String, Object> query, byte[] data,
                              String contentType, TypeReference<T> type) {
        if (data == null) {
            throw new IllegalArgumentException("data must not be null");
        }
        if (contentType == null || contentType.isEmpty()) {
            throw new IllegalArgumentException("contentType is required (e.g. image/png)");
        }
        return send(method, path, query, HttpRequest.BodyPublishers.ofByteArray(data), contentType, null, false, null, type);
    }

    /**
     * A JSON request with extra per-call headers (for example the end user's forwarded IP on the
     * Better Auth exchange). An {@code authorization} entry is ignored: the Bearer secret is never replaced.
     */
    public <T> T requestWithHeaders(String method,
                                    String path,
                                    Map<String, Object> query,
                                    Object body,
                                    String idempotencyKey,
                                    Map<String, String> headers,
                                    TypeReference<T> type) {
        if (body != null) {
            String json = writeBody(body);
            return send(method, path, query, HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8),
                    "application/json", idempotencyKey, false, headers, type);
        }
        return send(method, path, query, HttpRequest.BodyPublishers.noBody(), null, idempotencyKey, false, headers, type);
    }

    // ---- The one request path ---------------------------------------------

    public <T> T request(String method,
                         String path,
                         Map<String, Object> query,
                         Object body,
                         String idempotencyKey,
                         boolean raw,
                         TypeReference<T> type) {
        if (body != null) {
            String json = writeBody(body);
            return send(method, path, query, HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8),
                    "application/json", idempotencyKey, raw, null, type);
        }
        return send(method, path, query, HttpRequest.BodyPublishers.noBody(), null, idempotencyKey, raw, null, type);
    }

    private <T> T send(String method,
                       String path,
                       Map<String, Object> query,
                       HttpRequest.BodyPublisher publisher,
                       String contentType,
                       String idempotencyKey,
                       boolean raw,
                       Map<String, String> extraHeaders,
                       TypeReference<T> type) {
        if (secretKey == null || secretKey.isEmpty()) {
            throw new IllegalStateException(missingKeyMessage != null ? missingKeyMessage
                    : "No API key is configured for this call.");
        }
        boolean dry = dryRun && !"GET".equalsIgnoreCase(method);
        if (dry) {
            Map<String, Object> withDry = new java.util.LinkedHashMap<>();
            if (query != null) {
                withDry.putAll(query);
            }
            withDry.put("dry_run", true);
            query = withDry;
        }
        URI uri = URI.create(baseUrl + (path.startsWith("/") ? path : "/" + path) + serializeQuery(query));

        HttpRequest.Builder builder = HttpRequest.newBuilder(uri)
                .timeout(timeout)
                .header("authorization", "Bearer " + secretKey)
                .header("accept", "application/json");

        if (contentType != null) {
            builder.header("content-type", contentType);
        }
        if (idempotencyKey != null && !idempotencyKey.isEmpty() && !dry) {
            builder.header("idempotency-key", idempotencyKey);
        }
        if (traceparent != null && !traceparent.isEmpty()) {
            builder.header("traceparent", traceparent);
        }
        if (secretRecipient != null && !secretRecipient.isEmpty()) {
            builder.header("x-secret-recipient", secretRecipient);
        }
        if (extraHeaders != null) {
            for (Map.Entry<String, String> h : extraHeaders.entrySet()) {
                if (h.getKey() == null || h.getValue() == null || h.getValue().isEmpty()
                        || "authorization".equalsIgnoreCase(h.getKey())) {
                    continue;
                }
                builder.header(h.getKey(), h.getValue());
            }
        }
        if (strict != null && !"GET".equalsIgnoreCase(method)) {
            builder.header(STRICT_HEADER, strict ? "true" : "false");
        }
        builder.method(method, publisher);

        HttpResponse<String> response;
        try {
            response = http.send(builder.build(), HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new AtlasConnectionException("Atlas API request to " + uri + " failed: " + e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new AtlasConnectionException("Atlas API request to " + uri + " was interrupted", e);
        }

        int status = response.statusCode();
        String text = response.body();
        response.headers().firstValue(UNKNOWN_FIELDS_HEADER).ifPresent(v -> reportUnknown(method, path, v));

        if (status < 200 || status >= 300) {
            throw toApiError(status, text);
        }
        if (status == 204 || text == null || text.isEmpty()) {
            return null;
        }
        if (raw) {
            @SuppressWarnings("unchecked")
            T asText = (T) text;
            return asText;
        }
        try {
            JavaType jt = mapper.getTypeFactory().constructType(type);
            return mapper.readValue(text, jt);
        } catch (IOException e) {
            throw new AtlasConnectionException("Failed to decode Atlas API response from " + uri, e);
        }
    }

    private void reportUnknown(String method, String path, String header) {
        List<String> fields = new ArrayList<>();
        for (String f : header.split(",")) {
            String trimmed = f.trim();
            if (!trimmed.isEmpty()) {
                fields.add(trimmed);
            }
        }
        if (fields.isEmpty()) {
            return;
        }
        UnknownFieldsWarning warning = new UnknownFieldsWarning(method, path, List.copyOf(fields));
        if (onUnknownFields != null) {
            onUnknownFields.accept(warning);
        } else {
            LOG.log(System.Logger.Level.WARNING, "Atlas ignored unknown body field(s) on {0} {1}: {2}",
                    method, path, String.join(", ", fields));
        }
    }

    private String writeBody(Object body) {
        try {
            return mapper.writeValueAsString(body);
        } catch (IOException e) {
            throw new AtlasConnectionException("Failed to encode request body", e);
        }
    }

    /** Serialize a query map: drop nulls, spread collections/arrays, JSON booleans. */
    static String serializeQuery(Map<String, Object> query) {
        if (query == null || query.isEmpty()) {
            return "";
        }
        List<String> parts = new ArrayList<>();
        for (Map.Entry<String, Object> e : query.entrySet()) {
            Object value = e.getValue();
            if (value == null) {
                continue;
            }
            if (value instanceof Collection<?> collection) {
                for (Object v : collection) {
                    if (v != null) {
                        parts.add(encodePair(e.getKey(), v));
                    }
                }
            } else if (value instanceof Object[] array) {
                for (Object v : array) {
                    if (v != null) {
                        parts.add(encodePair(e.getKey(), v));
                    }
                }
            } else {
                parts.add(encodePair(e.getKey(), value));
            }
        }
        return parts.isEmpty() ? "" : "?" + String.join("&", parts);
    }

    private static String encodePair(String key, Object value) {
        String rendered = value instanceof Boolean b ? (b ? "true" : "false") : String.valueOf(value);
        return URLEncoder.encode(key, StandardCharsets.UTF_8) + "=" + URLEncoder.encode(rendered, StandardCharsets.UTF_8);
    }

    /** Percent-encode a single path segment (an id, slug, provider name). */
    public static String enc(String segment) {
        // URLEncoder is form-encoding; make it path-safe (space -> %20, keep it simple).
        return URLEncoder.encode(segment, StandardCharsets.UTF_8).replace("+", "%20");
    }

    private AtlasApiException toApiError(int status, String text) {
        List<ErrorItem> errors = new ArrayList<>();
        String message = null;
        if (text != null && !text.isEmpty()) {
            try {
                Envelope parsed = mapper.readValue(text, Envelope.class);
                if (parsed != null && parsed.errors != null && !parsed.errors.isEmpty()) {
                    errors = parsed.errors;
                } else {
                    message = truncate(text);
                }
            } catch (IOException notJson) {
                message = truncate(text);
            }
        }
        if (errors.isEmpty()) {
            errors = List.of(new ErrorItem("UNKNOWN",
                    message != null ? message : "Atlas API request failed with status " + status,
                    null, null));
        }
        return AtlasApiException.of(status, errors);
    }

    private static String truncate(String text) {
        return text.length() > 500 ? text.substring(0, 500) : text;
    }

    private static final class Envelope {
        public List<ErrorItem> errors;
    }
}
