package net.atlasauth.atlas.spring.boot;

import net.atlasauth.atlas.AtlasClient;
import net.atlasauth.atlas.rp.RelyingParty;
import net.atlasauth.atlas.rp.RelyingPartyFilter;
import net.atlasauth.atlas.rp.RpConfig;
import net.atlasauth.atlas.spring.AtlasAutoConfiguration;
import net.atlasauth.atlas.spring.AtlasProperties;
import net.atlasauth.atlas.verify.SessionVerifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Auto-configuration of the Atlas Spring Boot starter. It builds on the {@link SessionVerifier} that
 * {@link AtlasAutoConfiguration} creates from {@code atlas.issuer} and {@code atlas.jwks-url}, and adds:
 *
 * <ul>
 *   <li>an {@link AtlasClient} when {@code atlas.secret-key} is set (the Backend API, with the secret key);</li>
 *   <li>the {@link AtlasSecurityFilter} (networkless verification into Spring Security) and, unless you declare your
 *       own {@link SecurityFilterChain}, a default stateless chain that requires a session except on
 *       {@code atlas.security.public-paths}; to use your own chain call {@link #apply(HttpSecurity, AtlasSecurityFilter)};</li>
 *   <li>the hosted-redirect sign-in ({@link RelyingParty} + {@link RelyingPartyFilter}) when {@code atlas.login.client-id} is set.</li>
 * </ul>
 *
 * Every bean is {@code @ConditionalOnMissingBean}: declare your own to replace it.
 */
@AutoConfiguration(after = {AtlasAutoConfiguration.class, SecurityAutoConfiguration.class})
@ConditionalOnClass({HttpSecurity.class, SessionVerifier.class})
@EnableConfigurationProperties({AtlasSecurityProperties.class, AtlasLoginProperties.class})
public class AtlasStarterAutoConfiguration {

    /** Add the Atlas filter to your own chain: {@code AtlasStarterAutoConfiguration.apply(http, atlasSecurityFilter)}. */
    public static HttpSecurity apply(HttpSecurity http, AtlasSecurityFilter filter) throws Exception {
        return http.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(AbstractHttpConfigurer::disable);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "atlas", name = "secret-key")
    public AtlasClient atlasClient(AtlasProperties properties) {
        AtlasClient.Builder b = AtlasClient.builder().secretKey(properties.getSecretKey());
        if (properties.getBapiBaseUrl() != null && !properties.getBapiBaseUrl().isEmpty()) {
            b.apiUrl(properties.getBapiBaseUrl());
        }
        return b.build();
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(SessionVerifier.class)
    @ConditionalOnProperty(prefix = "atlas.security", name = "enabled", havingValue = "true", matchIfMissing = true)
    public AtlasSecurityFilter atlasSecurityFilter(SessionVerifier verifier, AtlasSecurityProperties properties) {
        return new AtlasSecurityFilter(verifier, properties.getCookieName());
    }

    /** The filter is applied inside the security chain only: keep the servlet container from registering it a second time. */
    @Bean
    @ConditionalOnBean(AtlasSecurityFilter.class)
    public FilterRegistrationBean<AtlasSecurityFilter> atlasSecurityFilterRegistration(AtlasSecurityFilter filter) {
        FilterRegistrationBean<AtlasSecurityFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }

    @Bean
    @ConditionalOnMissingBean(SecurityFilterChain.class)
    @ConditionalOnBean(AtlasSecurityFilter.class)
    public SecurityFilterChain atlasSecurityFilterChain(HttpSecurity http, AtlasSecurityFilter filter, AtlasSecurityProperties properties) throws Exception {
        apply(http, filter);
        String[] publicPaths = properties.getPublicPaths().toArray(String[]::new);
        http.authorizeHttpRequests(a -> a.requestMatchers(publicPaths).permitAll().anyRequest().authenticated());
        http.httpBasic(AbstractHttpConfigurer::disable).formLogin(AbstractHttpConfigurer::disable);
        http.exceptionHandling(e -> e.authenticationEntryPoint((req, res, ex) -> {
            res.setStatus(401);
            res.setContentType("application/json");
            res.getWriter().write("{\"error\":\"unauthorized\"}");
        }));
        return http.build();
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "atlas.login", name = "client-id")
    public RelyingParty atlasRelyingParty(AtlasLoginProperties login, AtlasProperties atlas) {
        RpConfig.Builder b = RpConfig.builder(atlas.getIssuer(), login.getClientId(), login.getRedirectUri(), login.getSecret())
                .scope(login.getScope()).afterSignInPath(login.getAfterSignInPath()).serverSignOut(login.isServerSignOut());
        if (login.getClientSecret() != null) {
            b.clientSecret(login.getClientSecret());
        }
        if (login.getPostLogoutRedirectUri() != null) {
            b.postLogoutRedirectUri(login.getPostLogoutRedirectUri());
        }
        if (atlas.getSecretKey() != null && !atlas.getSecretKey().isEmpty()) {
            b.secretKey(atlas.getSecretKey());
        }
        if (atlas.getBapiBaseUrl() != null && !atlas.getBapiBaseUrl().isEmpty()) {
            b.apiUrl(atlas.getBapiBaseUrl());
        }
        return new RelyingParty(b.build());
    }

    @Bean
    @ConditionalOnBean(RelyingParty.class)
    @ConditionalOnMissingBean(name = "atlasLoginFilterRegistration")
    public FilterRegistrationBean<RelyingPartyFilter> atlasLoginFilterRegistration(RelyingParty rp, AtlasLoginProperties login) {
        RelyingPartyFilter.Builder f = RelyingPartyFilter.builder(rp).signInPath(login.getSignInPath()).signOutPath(login.getSignOutPath())
                .requireSignIn(login.getProtectedPaths().toArray(String[]::new));
        FilterRegistrationBean<RelyingPartyFilter> registration = new FilterRegistrationBean<>(f.build());
        registration.addUrlPatterns("/*");
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE + 50);
        return registration;
    }
}
