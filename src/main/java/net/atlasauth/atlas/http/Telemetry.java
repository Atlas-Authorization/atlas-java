package net.atlasauth.atlas.http;

import java.time.Duration;

/**
 * An opt-in hook the transport calls around every Backend API request, so spans and metrics can be recorded
 * without the SDK depending on a telemetry library. The {@code atlas-java-otel} module adapts it to
 * OpenTelemetry. When none is configured the transport pays one null check per call.
 *
 * <p>Implementations must be thread-safe and must never throw: a failing hook is swallowed so telemetry can
 * never break an API call.
 */
public interface Telemetry {

    /** Start observing one logical request (all retries included). {@code path} is the path without the query string. */
    Span start(String method, String path);

    /** One logical request. */
    interface Span {
        /** An attempt failed with {@code status} ({@code 0} for a network error) and will be retried after {@code delay}. */
        default void retry(int attempt, int status, Duration delay) {
        }

        /** The request finished: {@code status} is the final HTTP status ({@code 0} when no response arrived), {@code error} the failure or null. */
        void end(int status, Throwable error);
    }
}
