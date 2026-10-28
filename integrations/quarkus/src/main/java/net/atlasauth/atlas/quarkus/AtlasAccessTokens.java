package net.atlasauth.atlas.quarkus;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import net.atlasauth.atlas.verify.AccessTokenVerifier;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.List;
import java.util.Optional;

/**
 * Holds the OAuth access-token verifier when {@code atlas.access-token.audience} is configured:
 * <pre>
 * atlas.access-token.audience=https://api.example.com
 * atlas.access-token.required-scopes=orders:read
 * </pre>
 * Without it only sessions are accepted. Tests and custom setups can call {@link #of}.
 */
@ApplicationScoped
public class AtlasAccessTokens {

    @ConfigProperty(name = "atlas.issuer")
    String issuer;

    @ConfigProperty(name = "atlas.jwks-url")
    String jwksUrl;

    @ConfigProperty(name = "atlas.access-token.audience")
    Optional<String> audience;

    @ConfigProperty(name = "atlas.access-token.required-scopes")
    Optional<List<String>> requiredScopes;

    private AccessTokenVerifier verifier;

    public AtlasAccessTokens() {
    }

    private AtlasAccessTokens(AccessTokenVerifier verifier) {
        this.verifier = verifier;
    }

    public static AtlasAccessTokens of(AccessTokenVerifier verifier) {
        return new AtlasAccessTokens(verifier);
    }

    @PostConstruct
    void init() {
        if (verifier == null && audience.isPresent()) {
            AccessTokenVerifier.Builder b = AccessTokenVerifier.builder().issuer(issuer).jwksUrl(jwksUrl).audience(audience.get());
            requiredScopes.ifPresent(s -> b.requiredScopes(s.toArray(new String[0])));
            verifier = b.build();
        }
    }

    /** The verifier, or null when access tokens are not enabled. */
    public AccessTokenVerifier verifier() {
        return verifier;
    }
}
