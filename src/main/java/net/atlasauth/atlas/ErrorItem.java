package net.atlasauth.atlas;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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
        Map<String, Object> meta) {
}
