package net.atlasauth.atlas.micronaut;

import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.security.token.reader.TokenReader;
import jakarta.inject.Singleton;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Finds the Atlas session token on a request: {@code Authorization: Bearer} first, then the
 * {@code __session} cookie (configurable with {@code atlas.cookie-name}).
 */
@Singleton
@Requires(property = "atlas.issuer")
public class AtlasTokenReader implements TokenReader<HttpRequest<?>> {

    private static final Pattern BEARER = Pattern.compile("^Bearer\\s+(\\S+)$", Pattern.CASE_INSENSITIVE);

    private final String cookieName;

    public AtlasTokenReader(AtlasConfiguration config) {
        this.cookieName = config.getCookieName();
    }

    @Override
    public Optional<String> findToken(HttpRequest<?> request) {
        String authorization = request.getHeaders().get("Authorization");
        if (authorization != null) {
            Matcher m = BEARER.matcher(authorization.trim());
            if (m.matches()) {
                return Optional.of(m.group(1));
            }
        }
        return request.getCookies().findCookie(cookieName)
                .map(cookie -> cookie.getValue())
                .filter(value -> !value.isEmpty());
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE;
    }
}
