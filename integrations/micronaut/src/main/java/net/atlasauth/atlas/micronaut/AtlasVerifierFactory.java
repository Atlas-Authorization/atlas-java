package net.atlasauth.atlas.micronaut;

import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.annotation.Secondary;
import jakarta.inject.Singleton;
import net.atlasauth.atlas.verify.AccessTokenVerifier;
import net.atlasauth.atlas.verify.SessionVerifier;

/**
 * Builds the {@link SessionVerifier} (and, with {@code atlas.access-token.audience}, the
 * {@link AccessTokenVerifier}) from {@code atlas.*} configuration. Declare your own beans to replace them.
 */
@Factory
@Requires(property = "atlas.issuer")
public class AtlasVerifierFactory {

    @Singleton
    @Secondary
    SessionVerifier atlasSessionVerifier(AtlasConfiguration config) {
        SessionVerifier.Builder builder = SessionVerifier.builder().issuer(config.getIssuer()).jwksUrl(config.getJwksUrl());
        if (config.getAuthorizedParties() != null) {
            builder.authorizedParties(config.getAuthorizedParties());
        }
        return builder.build();
    }

    @Singleton
    @Secondary
    @Requires(property = "atlas.access-token.audience")
    AccessTokenVerifier atlasAccessTokenVerifier(AtlasConfiguration config) {
        AtlasConfiguration.AccessToken at = config.getAccessToken();
        AccessTokenVerifier.Builder builder = AccessTokenVerifier.builder()
                .issuer(config.getIssuer()).jwksUrl(config.getJwksUrl()).audience(at.getAudience());
        if (at.getRequiredScopes() != null && !at.getRequiredScopes().isEmpty()) {
            builder.requiredScopes(at.getRequiredScopes().toArray(new String[0]));
        }
        return builder.build();
    }
}
