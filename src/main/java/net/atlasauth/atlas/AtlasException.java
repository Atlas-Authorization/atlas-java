package net.atlasauth.atlas;

/**
 * Base type for every error the Atlas SDK raises.
 *
 * <p>Concrete failures are one of two families:
 * <ul>
 *   <li>{@link AtlasApiException} (and its status-specific subclasses) — the server
 *       answered with a non-2xx and the &sect;9.1 error envelope.</li>
 *   <li>{@link AtlasConnectionException} — the request never produced an HTTP
 *       response (network failure, timeout, malformed URL).</li>
 * </ul>
 */
public abstract class AtlasException extends RuntimeException {

    protected AtlasException(String message) {
        super(message);
    }

    protected AtlasException(String message, Throwable cause) {
        super(message, cause);
    }
}
