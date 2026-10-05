package net.atlasauth.atlas.spring;

import net.atlasauth.atlas.verify.SessionVerifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.web.filter.OncePerRequestFilter;

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
        return builder.build();
    }

    @Bean
    @ConditionalOnMissingBean(name = "atlasAuthenticationFilterRegistration")
    public FilterRegistrationBean<AtlasAuthenticationFilter> atlasAuthenticationFilterRegistration(
            SessionVerifier atlasSessionVerifier, AtlasProperties properties) {
        FilterRegistrationBean<AtlasAuthenticationFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new AtlasAuthenticationFilter(atlasSessionVerifier, properties));
        registration.addUrlPatterns("/*");
        // Run before application filters; after Spring Security's filter chain
        // (REQUEST_WRAPPER_FILTER_MAX_ORDER) so Security, when present, owns the
        // SecurityContext lifecycle around us.
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE + 100);
        return registration;
    }
}
