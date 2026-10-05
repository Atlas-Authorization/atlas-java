package net.atlasauth.atlas.spring;

import net.atlasauth.atlas.verify.SessionVerifier;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.web.servlet.FilterRegistrationBean;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies the auto-configuration contract: it stays dormant until the required
 * properties are present, then contributes the verifier and the filter.
 */
class AtlasAutoConfigurationTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(AtlasAutoConfiguration.class));

    @Test
    void doesNothingWithoutRequiredProperties() {
        runner.run(context -> assertThat(context).doesNotHaveBean(SessionVerifier.class));
    }

    @Test
    void contributesVerifierAndFilterWhenConfigured() {
        runner.withPropertyValues(
                        "atlas.jwks-url=https://api.atlasauth.net/v1/jwks",
                        "atlas.issuer=https://clerk.example.com")
                .run(context -> {
                    assertThat(context).hasSingleBean(SessionVerifier.class);
                    assertThat(context).hasSingleBean(FilterRegistrationBean.class);
                    assertThat(context).hasSingleBean(AtlasProperties.class);

                    @SuppressWarnings("unchecked")
                    FilterRegistrationBean<AtlasAuthenticationFilter> registration =
                            context.getBean(FilterRegistrationBean.class);
                    assertThat(registration.getFilter()).isInstanceOf(AtlasAuthenticationFilter.class);
                    assertThat(registration.getUrlPatterns()).contains("/*");
                });
    }

    @Test
    void bindsOptionalProperties() {
        runner.withPropertyValues(
                        "atlas.jwks-url=https://api.atlasauth.net/v1/jwks",
                        "atlas.issuer=https://clerk.example.com",
                        "atlas.cookie-name=my_session",
                        "atlas.protected-paths=/api/**,/admin/**")
                .run(context -> {
                    AtlasProperties properties = context.getBean(AtlasProperties.class);
                    assertThat(properties.getCookieName()).isEqualTo("my_session");
                    assertThat(properties.getProtectedPaths()).containsExactly("/api/**", "/admin/**");
                });
    }

    @Test
    void backsOffWhenApplicationDeclaresItsOwnVerifier() {
        runner.withPropertyValues(
                        "atlas.jwks-url=https://api.atlasauth.net/v1/jwks",
                        "atlas.issuer=https://clerk.example.com")
                .withBean(SessionVerifier.class, () -> SessionVerifier.builder()
                        .jwksUrl("https://custom/jwks")
                        .issuer("https://custom")
                        .build())
                .run(context -> assertThat(context).hasSingleBean(SessionVerifier.class));
    }
}
