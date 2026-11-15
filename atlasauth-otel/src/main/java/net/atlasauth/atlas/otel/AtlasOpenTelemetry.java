package net.atlasauth.atlas.otel;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.metrics.DoubleHistogram;
import io.opentelemetry.api.metrics.LongCounter;
import io.opentelemetry.api.metrics.Meter;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import net.atlasauth.atlas.http.Telemetry;

import java.time.Duration;

/**
 * OpenTelemetry for atlas-java: one client span per Backend API request (retries included, each retry an event on
 * it) and three metrics. Opt in on the client; atlas-java itself has no OpenTelemetry dependency.
 *
 * <pre>{@code
 * AtlasClient atlas = AtlasClient.builder().secretKey(key)
 *     .telemetry(new AtlasOpenTelemetry(openTelemetry))
 *     .build();
 * }</pre>
 *
 * <p>Spans are named {@code Atlas <METHOD>} with {@code http.request.method}, {@code url.path},
 * {@code http.response.status_code}, {@code atlas.retry.count}; an error status or a network failure marks the span
 * as an error. Metrics: {@code atlas.client.requests} (counter), {@code atlas.client.request.duration} (histogram,
 * seconds) and {@code atlas.client.retries} (counter), tagged by method and status. The path is the literal request
 * path (ids included): drop the attribute in a view if that is high-cardinality for you; metrics do not carry it.
 */
public final class AtlasOpenTelemetry implements Telemetry {

    private static final AttributeKey<String> METHOD = AttributeKey.stringKey("http.request.method");
    private static final AttributeKey<String> PATH = AttributeKey.stringKey("url.path");
    private static final AttributeKey<Long> STATUS = AttributeKey.longKey("http.response.status_code");
    private static final AttributeKey<Long> RETRIES = AttributeKey.longKey("atlas.retry.count");

    private final Tracer tracer;
    private final LongCounter requests;
    private final LongCounter retries;
    private final DoubleHistogram duration;

    public AtlasOpenTelemetry(OpenTelemetry openTelemetry) {
        this.tracer = openTelemetry.getTracer("net.atlasauth.atlas");
        Meter meter = openTelemetry.getMeter("net.atlasauth.atlas");
        this.requests = meter.counterBuilder("atlas.client.requests").setDescription("Atlas API requests").build();
        this.retries = meter.counterBuilder("atlas.client.retries").setDescription("Atlas API request retries").build();
        this.duration = meter.histogramBuilder("atlas.client.request.duration").setUnit("s").setDescription("Atlas API request duration, retries included").build();
    }

    @Override
    public Telemetry.Span start(String method, String path) {
        io.opentelemetry.api.trace.Span span = tracer.spanBuilder("Atlas " + method).setSpanKind(SpanKind.CLIENT)
                .setAttribute(METHOD, method).setAttribute(PATH, path).startSpan();
        long started = System.nanoTime();
        return new Telemetry.Span() {
            private long retryCount;

            @Override
            public void retry(int attempt, int status, Duration delay) {
                retryCount = attempt;
                retries.add(1, Attributes.of(METHOD, method, STATUS, (long) status));
                span.addEvent("retry", Attributes.of(AttributeKey.longKey("atlas.retry.attempt"), (long) attempt, STATUS, (long) status,
                        AttributeKey.longKey("atlas.retry.delay_ms"), delay.toMillis()));
            }

            @Override
            public void end(int status, Throwable error) {
                span.setAttribute(STATUS, (long) status);
                span.setAttribute(RETRIES, retryCount);
                if (error != null) {
                    span.recordException(error);
                    span.setStatus(StatusCode.ERROR, String.valueOf(error.getMessage()));
                }
                Attributes tags = Attributes.of(METHOD, method, STATUS, (long) status);
                requests.add(1, tags);
                duration.record((System.nanoTime() - started) / 1e9, tags);
                span.end();
            }
        };
    }
}
