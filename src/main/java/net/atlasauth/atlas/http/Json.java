package net.atlasauth.atlas.http;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;

/**
 * The single configuration point for JSON on the wire.
 *
 * <p>The Backend API speaks snake_case; SDK models are written in idiomatic Java
 * camelCase and bridged by {@link PropertyNamingStrategies#SNAKE_CASE}. A field
 * that is camelCase <em>on the wire</em> (e.g. {@code roleKey} on an SSO claim
 * mapping) overrides the strategy with an explicit {@code @JsonProperty}.
 *
 * <ul>
 *   <li>{@code NON_NULL} property inclusion — a request body serializes only the fields a
 *       caller actually set, matching how optionals are omitted on the wire.</li>
 *   <li>Unknown properties are ignored — a new server field never breaks an old
 *       client.</li>
 *   <li>Field visibility is opened so mutable request-body POJOs need no getters.</li>
 * </ul>
 */
public final class Json {

    private Json() {
    }

    public static ObjectMapper mapper() {
        return new ObjectMapper()
                .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
                // Unset (null) properties are omitted, but a null placed in a map is kept:
                // that is how a caller sends an explicit null to clear a stored value.
                .setDefaultPropertyInclusion(
                        JsonInclude.Value.construct(JsonInclude.Include.NON_NULL, JsonInclude.Include.ALWAYS))
                .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
}
