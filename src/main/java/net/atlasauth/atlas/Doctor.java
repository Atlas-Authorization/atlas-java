package net.atlasauth.atlas;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.atlasauth.atlas.http.Transport;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Self-check for an Atlas integration: {@code atlas.doctor().run()} reports, per check, whether the keys are
 * well formed, the clock agrees with Atlas (session tokens fail on skewed clocks), the JWKS endpoint is
 * reachable, the service status is healthy, the secret key is accepted and the client config is sane. Nothing
 * is written and no secret is printed.
 *
 * <pre>{@code
 * Doctor.Report report = atlas.doctor().run();
 * System.out.println(report);
 * if (!report.ok()) System.exit(1);
 * }</pre>
 */
public final class Doctor {

    /** Outcome of one check. */
    public enum Level { OK, WARN, FAIL, SKIP }

    /** One line of the report. */
    public record Check(String name, Level level, String message) {
    }

    /** All checks of one run. */
    public record Report(List<Check> checks) {
        /** True when no check failed (warnings and skips are fine). */
        public boolean ok() {
            return checks.stream().noneMatch(c -> c.level() == Level.FAIL);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (Check c : checks) {
                sb.append(String.format("%-5s %-14s %s%n", c.level(), c.name(), c.message()));
            }
            return sb.toString();
        }
    }

    /** Skew above this is a warning (tokens near their expiry start failing), above {@link #SKEW_FAIL} an error. */
    static final Duration SKEW_WARN = Duration.ofSeconds(30);
    static final Duration SKEW_FAIL = Duration.ofMinutes(5);

    private final String secretKey;
    private final String platformKey;
    private final String apiUrl;
    private final HttpClient http;
    private final ObjectMapper mapper;
    private final Duration timeout;
    private final Transport transport;

    Doctor(String secretKey, String platformKey, String apiUrl, HttpClient http, ObjectMapper mapper, Duration timeout, Transport transport) {
        this.secretKey = secretKey;
        this.platformKey = platformKey;
        this.apiUrl = apiUrl.replaceAll("/+$", "");
        this.http = http;
        this.mapper = mapper;
        this.timeout = timeout;
        this.transport = transport;
    }

    /** Run every check; the JWKS URL defaults to {@code <apiUrl>/v1/jwks}. */
    public Report run() {
        return run(null);
    }

    /** As {@link #run()}, checking this JWKS URL (your instance's custom-domain issuer, for example). */
    public Report run(String jwksUrl) {
        List<Check> out = new ArrayList<>();
        out.add(checkKeys());
        out.add(checkConfig());
        out.add(checkClock());
        out.add(checkJwks(jwksUrl != null ? jwksUrl : apiUrl + "/v1/jwks"));
        out.add(checkStatus());
        out.add(checkWhoami());
        return new Report(List.copyOf(out));
    }

    private Check checkKeys() {
        boolean hasSecret = secretKey != null && !secretKey.isEmpty();
        boolean hasPlatform = platformKey != null && !platformKey.isEmpty();
        if (!hasSecret && !hasPlatform) {
            return new Check("keys", Level.FAIL, "no secret key or platform key is configured");
        }
        if (hasSecret && !KeyFormat.isSecretKey(secretKey)) {
            return new Check("keys", Level.FAIL, "the secret key is not in the sk_... format (is it a publishable key?)");
        }
        if (hasPlatform && !KeyFormat.isPlatformKey(platformKey)) {
            return new Check("keys", Level.FAIL, "the platform key is not in the psk_... format");
        }
        String env = hasSecret ? KeyFormat.parse(secretKey).environment() : null;
        return new Check("keys", Level.OK, (hasSecret ? "secret key" + (env != null ? " (" + env + ")" : "") : "no secret key")
                + (hasPlatform ? ", platform key" : ""));
    }

    private Check checkConfig() {
        URI u;
        try {
            u = URI.create(apiUrl);
        } catch (IllegalArgumentException e) {
            return new Check("config", Level.FAIL, "apiUrl is not a valid URL: " + apiUrl);
        }
        String host = u.getHost() == null ? "" : u.getHost();
        boolean local = host.equals("localhost") || host.equals("127.0.0.1") || host.equals("::1");
        if (!"https".equals(u.getScheme()) && !local) {
            return new Check("config", Level.WARN, "apiUrl uses " + u.getScheme() + ": keys are sent in clear text");
        }
        if (timeout.compareTo(Duration.ofSeconds(1)) < 0) {
            return new Check("config", Level.WARN, "timeout is under one second");
        }
        return new Check("config", Level.OK, apiUrl + ", timeout " + timeout.toSeconds() + "s");
    }

    private Check checkClock() {
        try {
            Instant before = Instant.now();
            HttpResponse<Void> res = http.send(get(apiUrl + "/v1/health"), HttpResponse.BodyHandlers.discarding());
            Instant after = Instant.now();
            String date = res.headers().firstValue("date").orElse(null);
            if (date == null) {
                return new Check("clock", Level.SKIP, "the server sent no Date header");
            }
            Instant server = ZonedDateTime.parse(date, DateTimeFormatter.RFC_1123_DATE_TIME).toInstant();
            Instant local = before.plus(Duration.between(before, after).dividedBy(2));
            Duration skew = Duration.between(local, server).abs();
            if (skew.compareTo(SKEW_FAIL) > 0) {
                return new Check("clock", Level.FAIL, "this machine's clock is " + skew.toSeconds() + "s off: session tokens will be rejected");
            }
            if (skew.compareTo(SKEW_WARN) > 0) {
                return new Check("clock", Level.WARN, "this machine's clock is " + skew.toSeconds() + "s off: tokens near expiry may fail");
            }
            return new Check("clock", Level.OK, "within " + Math.max(1, skew.toSeconds()) + "s of Atlas");
        } catch (IOException | RuntimeException e) {
            return new Check("clock", Level.FAIL, "could not reach " + apiUrl + ": " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new Check("clock", Level.FAIL, "interrupted");
        }
    }

    private Check checkJwks(String url) {
        try {
            HttpResponse<String> res = http.send(get(url), HttpResponse.BodyHandlers.ofString());
            if (res.statusCode() != 200) {
                return new Check("jwks", Level.FAIL, url + " answered " + res.statusCode());
            }
            JsonNode keys = mapper.readTree(res.body()).path("keys");
            if (!keys.isArray() || keys.isEmpty()) {
                return new Check("jwks", Level.FAIL, url + " has no keys");
            }
            return new Check("jwks", Level.OK, keys.size() + " signing key(s) at " + url);
        } catch (IOException | RuntimeException e) {
            return new Check("jwks", Level.FAIL, "could not read " + url + ": " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new Check("jwks", Level.FAIL, "interrupted");
        }
    }

    private Check checkStatus() {
        try {
            HttpResponse<String> res = http.send(get(apiUrl + "/v1/status"), HttpResponse.BodyHandlers.ofString());
            if (res.statusCode() != 200) {
                return new Check("status", Level.WARN, "the status endpoint answered " + res.statusCode());
            }
            JsonNode doc = mapper.readTree(res.body());
            String status = doc.path("status").asText("unknown");
            for (JsonNode comp : doc.path("components")) {
                if ("backend_api".equals(comp.path("id").asText()) && "major_outage".equals(comp.path("status").asText())) {
                    status = "major_outage";
                }
            }
            return switch (status) {
                case "operational" -> new Check("status", Level.OK, "all systems operational");
                case "major_outage" -> new Check("status", Level.FAIL, "Atlas reports a major outage");
                default -> new Check("status", Level.WARN, "Atlas reports " + status);
            };
        } catch (IOException | RuntimeException e) {
            return new Check("status", Level.WARN, "could not read the status endpoint: " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new Check("status", Level.WARN, "interrupted");
        }
    }

    private Check checkWhoami() {
        if (secretKey == null || secretKey.isEmpty()) {
            return new Check("credentials", Level.SKIP, "no secret key to test");
        }
        try {
            JsonNode who = transport.get("/v1/whoami", new com.fasterxml.jackson.core.type.TypeReference<JsonNode>() {
            });
            String type = who == null ? "" : who.path("type").asText("");
            return new Check("credentials", Level.OK, "the secret key is accepted" + (type.isEmpty() ? "" : " (" + type + ")"));
        } catch (AuthenticationException e) {
            return new Check("credentials", Level.FAIL, "the secret key was rejected (401)");
        } catch (AtlasException e) {
            return new Check("credentials", Level.FAIL, "whoami failed: " + e.getMessage());
        }
    }

    private HttpRequest get(String url) {
        return HttpRequest.newBuilder(URI.create(url)).timeout(timeout).header("accept", "application/json").GET().build();
    }
}
