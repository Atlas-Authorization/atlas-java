package net.atlasauth.atlas.spring;

import net.atlasauth.atlas.AtlasClient;
import net.atlasauth.atlas.betterauth.SessionBridge;
import net.atlasauth.atlas.verify.LegacyIssuer;
import net.atlasauth.atlas.verify.SessionVerifier;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.web.filter.OncePerRequestFilter;

import java.util.ArrayList;
import java.util.List;

/**
 * Wires Atlas session verification into a Spring Boot application with zero code.
 * It activates only when spring-web is on the classpath and
 * {@code atlas.jwks-url} and {@code atlas.issuer} are configured — so merely
 * depending on {@code net.atlasauth:atlas-java} costs a non-Spring app nothing.
 *
 * <p>It contributes a {@link SessionVerifier} built from {@link AtlasProperties}
 * and registers an {@link AtlasAuthenticationFilter} early in the chain. Both are
 * {@code @ConditionalOnMissingBean}, so an application can override either by
 * declaring its own.
 */
@AutoConfiguration
@ConditionalOnClass(OncePerRequestFilter.class)
@EnableConfigurationProperties(AtlasProperties.class)
@ConditionalOnProperty(prefix = "atlas", name = {"jwks-url", "issuer"})
public class AtlasAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public SessionVerifier atlasSessionVerifier(AtlasProperties properties) {
        SessionVerifier.Builder builder = SessionVerifier.builder()
                .jwksUrl(properties.getJwksUrl())
                .issuer(properties.getIssuer());
        if (properties.getSecretKey() != null) {
            builder.secretKey(properties.getSecretKey());
        }
        if (properties.getBapiBaseUrl() != null) {
            builder.bapiBaseUrl(properties.getBapiBaseUrl());
        }
        if (!properties.getLegacyIssuers().isEmpty()) {
            List<LegacyIssuer> issuers = new ArrayList<>();
            for (AtlasProperties.LegacyIssuerProperties p : properties.getLegacyIssuers()) {
                LegacyIssuer.Builder b = LegacyIssuer.builder(p.getIssuer()).until(p.getUntil()).jwksUrl(p.getJwksUrl());
                if (p.getAudience() != null && !p.getAudience().isEmpty()) {
                    b.audience(p.getAudience());
                }
                if (p.getAlgorithms() != null && !p.getAlgorithms().isEmpty()) {
                    b.algorithms(p.getAlgorithms());
                }
                issuers.add(b.build());
            }
            builder.legacyIssuers(issuers);
        }
        return builder.build();
    }

    /** The Better Auth session bridge, when {@code atlas.better-auth-bridge.enabled=true}. */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "atlas.better-auth-bridge", name = "enabled", havingValue = "true")
    public SessionBridge atlasBetterAuthBridge(AtlasProperties properties) {
        if (properties.getSecretKey() == null || properties.getSecretKey().isEmpty()) {
            throw new IllegalStateException("atlas.better-auth-bridge needs atlas.secret-key: the exchange goes "
                    + "through the Backend API with the instance secret key.");
        }
        AtlasClient.Builder client = AtlasClient.builder().secretKey(properties.getSecretKey());
        if (properties.getBapiBaseUrl() != null && !properties.getBapiBaseUrl().isEmpty()) {
            client.apiUrl(properties.getBapiBaseUrl());
        }
        AtlasProperties.BetterAuthBridge cfg = properties.getBetterAuthBridge();
        return SessionBridge.builder(client.build())
                .cookiePrefix(cfg.getCookiePrefix())
                .cookieName(cfg.getCookieName())
                .betterAuthCookieDomain(cfg.getBetterAuthCookieDomain())
                .secure(cfg.isSecure())
                .build();
    }

    @Bean
    @ConditionalOnMissingBean(name = "atlasAuthenticationFilterRegistration")
    public FilterRegistrationBean<AtlasAuthenticationFilter> atlasAuthenticationFilterRegistration(
            SessionVerifier atlasSessionVerifier, AtlasProperties properties,
            ObjectProvider<SessionBridge> atlasBetterAuthBridge) {
        FilterRegistrationBean<AtlasAuthenticationFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new AtlasAuthenticationFilter(atlasSessionVerifier, properties,
                atlasBetterAuthBridge.getIfAvailable()));
        registration.addUrlPatterns("/*");
        // Run before application filters; after Spring Security's filter chain
        // (REQUEST_WRAPPER_FILTER_MAX_ORDER) so Security, when present, owns the
        // SecurityContext lifecycle around us.
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE + 100);
        return registration;
    }
}
