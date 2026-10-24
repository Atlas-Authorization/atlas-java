package net.atlasauth.atlas;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Map;

/**
 * One entry of the Atlas Backend API error envelope (&sect;9.1):
 *
 * <pre>{ "errors": [ { "code", "message", "param?", "meta?" } ] }</pre>
 *
 * {@code code} is the stable, machine-readable part of the contract — integrators
 * branch on it ({@code LAST_ADMIN}, {@code NOT_FOUND}, {@code SCOPE_MISSING}, ...).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ErrorItem(
        String code,
        String message,
        String param,
        Map<String, Object> meta,
        /** The code's page in the error reference ({@code https://atlasauth.net/errors/<code>}). */ String docsUrl,
        /** On {@code ROUTE_NOT_FOUND} only: the closest documented paths. */ List<String> didYouMean,
        /** On {@code ROUTE_NOT_FOUND} only: the methods the path does support. */ List<String> allowedMethods) {

    /** The shape without {@code docsUrl}, {@code didYouMean}, {@code allowedMethods}. */
    public ErrorItem(String code, String message, String param, Map<String, Object> meta) {
        this(code, message, param, meta, null, null, null);
    }
}
