package net.atlasauth.atlas.otel;

import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.sdk.testing.junit5.OpenTelemetryExtension;
import io.opentelemetry.sdk.trace.data.SpanData;
import net.atlasauth.atlas.http.Telemetry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AtlasOpenTelemetryTest {

    @RegisterExtension
    static final OpenTelemetryExtension OTEL = OpenTelemetryExtension.create();

    @Test
    void recordsOneClientSpanPerRequestWithRetryEventsAndErrorStatus() {
        Telemetry.Span span = new AtlasOpenTelemetry(OTEL.getOpenTelemetry()).start("GET", "/v1/users");
        span.retry(1, 503, Duration.ofMillis(10));
        span.end(503, new RuntimeException("boom"));
        List<SpanData> spans = OTEL.getSpans();
        assertEquals(1, spans.size());
        SpanData s = spans.get(0);
        assertEquals("Atlas GET", s.getName());
        assertEquals(StatusCode.ERROR, s.getStatus().getStatusCode());
        assertEquals(1, s.getEvents().stream().filter(e -> e.getName().equals("retry")).count());
        assertTrue(s.getAttributes().asMap().values().stream().anyMatch(v -> "/v1/users".equals(v)));
    }
}
