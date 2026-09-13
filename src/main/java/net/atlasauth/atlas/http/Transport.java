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

    private final String secretKey;
    private final String baseUrl;
    private final HttpClient http;
    private final ObjectMapper mapper;
    private final Duration timeout;

    public Transport(String secretKey, String baseUrl, HttpClient http, ObjectMapper mapper, Duration timeout) {
        this.secretKey = secretKey;
        this.baseUrl = baseUrl.replaceAll("/+$", "");
        this.http = http;
        this.mapper = mapper;
        this.timeout = timeout;
    }

    public ObjectMapper mapper() {
        return mapper;
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
        return request("GET", path, null, null, null, true, new TypeReference<String>() {
        });
    }

    // ---- The one request path ---------------------------------------------

    public <T> T request(String method,
                         String path,
                         Map<String, Object> query,
                         Object body,
                         String idempotencyKey,
                         boolean raw,
                         TypeReference<T> type) {
        URI uri = URI.create(baseUrl + (path.startsWith("/") ? path : "/" + path) + serializeQuery(query));

        HttpRequest.Builder builder = HttpRequest.newBuilder(uri)
                .timeout(timeout)
                .header("authorization", "Bearer " + secretKey)
                .header("accept", "application/json");

        HttpRequest.BodyPublisher publisher;
        if (body != null) {
            String json = writeBody(body);
            publisher = HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8);
            builder.header("content-type", "application/json");
        } else {
            publisher = HttpRequest.BodyPublishers.noBody();
        }
        if (idempotencyKey != null && !idempotencyKey.isEmpty()) {
            builder.header("idempotency-key", idempotencyKey);
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
