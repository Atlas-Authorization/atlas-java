package net.atlasauth.atlas;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

/**
 * A tiny in-process Backend API stand-in built on the JDK's {@link HttpServer}.
 * Records the one request it receives (method, path, headers, body) for
 * assertions and replies with a canned status + JSON body the test sets. One
 * request per test keeps it trivial and network-free.
 */
final class MockBackend implements AutoCloseable {

    private final HttpServer server;

    volatile String method;
    volatile String path;
    volatile String rawPath;
    volatile String rawQuery;
    volatile String authorization;
    volatile String idempotencyKey;
    volatile String requestBody;
    volatile byte[] requestBytes;
    volatile com.sun.net.httpserver.Headers requestHeaders;
    final java.util.Map<String, String> responseHeaders = new java.util.concurrent.ConcurrentHashMap<>();

    volatile int status = 200;
    volatile String responseBody = "{}";

    private MockBackend(HttpServer server) {
        this.server = server;
    }

    static MockBackend start() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        MockBackend mock = new MockBackend(server);
        server.createContext("/", mock::handle);
        server.setExecutor(null);
        server.start();
        return mock;
    }

    private void handle(HttpExchange exchange) throws IOException {
        this.method = exchange.getRequestMethod();
        this.path = exchange.getRequestURI().getPath();
        this.rawPath = exchange.getRequestURI().getRawPath();
        this.rawQuery = exchange.getRequestURI().getRawQuery();
        this.authorization = exchange.getRequestHeaders().getFirst("authorization");
        this.idempotencyKey = exchange.getRequestHeaders().getFirst("idempotency-key");
        this.requestHeaders = exchange.getRequestHeaders();
        this.requestBytes = exchange.getRequestBody().readAllBytes();
        this.requestBody = new String(requestBytes, StandardCharsets.UTF_8);
        responseHeaders.forEach((k, v) -> exchange.getResponseHeaders().add(k, v));

        byte[] payload = responseBody.getBytes(StandardCharsets.UTF_8);
        if (status == 204 || payload.length == 0) {
            exchange.sendResponseHeaders(status, -1);
        } else {
            exchange.getResponseHeaders().add("content-type", "application/json");
            exchange.sendResponseHeaders(status, payload.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(payload);
            }
        }
        exchange.close();
    }

    /** A request header as received (case-insensitive), or null. */
    String header(String name) {
        return requestHeaders == null ? null : requestHeaders.getFirst(name);
    }

    void reply(int status, String body) {
        this.status = status;
        this.responseBody = body;
    }

    String baseUrl() {
        return "http://127.0.0.1:" + server.getAddress().getPort();
    }

    @Override
    public void close() {
        server.stop(0);
    }
}
