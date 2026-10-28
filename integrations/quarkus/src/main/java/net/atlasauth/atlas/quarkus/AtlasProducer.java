package net.atlasauth.atlas.quarkus;

import io.quarkus.arc.DefaultBean;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;
import net.atlasauth.atlas.verify.SessionVerifier;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.List;
import java.util.Optional;

/**
 * Builds the {@link SessionVerifier} from configuration:
 * <pre>
 * atlas.issuer=https://auth.example.com
 * atlas.jwks-url=https://auth.example.com/.well-known/jwks.json
 * atlas.authorized-parties=https://app.example.com
 * </pre>
 * Declare your own {@code SessionVerifier} bean to replace it.
 */
@ApplicationScoped
public class AtlasProducer {

    @ConfigProperty(name = "atlas.issuer")
    String issuer;

    @ConfigProperty(name = "atlas.jwks-url")
    String jwksUrl;

    @ConfigProperty(name = "atlas.authorized-parties")
    Optional<List<String>> authorizedParties;

    @Produces
    @DefaultBean
    @Singleton // not normal-scoped: SessionVerifier is final and cannot be proxied
    public SessionVerifier atlasSessionVerifier() {
        SessionVerifier.Builder builder = SessionVerifier.builder().issuer(issuer).jwksUrl(jwksUrl);
        authorizedParties.ifPresent(builder::authorizedParties);
        return builder.build();
    }
}
