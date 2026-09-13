package net.atlasauth.atlas;

/**
 * Thrown when a request never produced an HTTP response — a network failure,
 * timeout, or interruption. Distinct from {@link AtlasApiException}, which always
 * carries a server status.
 */
public class AtlasConnectionException extends AtlasException {
    public AtlasConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
