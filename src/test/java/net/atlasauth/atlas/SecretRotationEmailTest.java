package net.atlasauth.atlas;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.atlasauth.atlas.http.Json;
import net.atlasauth.atlas.models.AllowedOriginEntry;
import net.atlasauth.atlas.models.BrandingResponse;
import net.atlasauth.atlas.models.DevelopmentEmail;
import net.atlasauth.atlas.models.DevelopmentEmailBody;
import net.atlasauth.atlas.models.DevelopmentEmailDisabled;
import net.atlasauth.atlas.models.EmailAddress;
import net.atlasauth.atlas.models.EmailProviderConfig;
import net.atlasauth.atlas.models.InstanceConfig;
import net.atlasauth.atlas.models.LoginReadiness;
import net.atlasauth.atlas.models.ManagedEmail;
import net.atlasauth.atlas.models.ManagedEmailBody;
import net.atlasauth.atlas.models.MessagingProvider;
import net.atlasauth.atlas.models.OAuthClientWithSecret;
import net.atlasauth.atlas.models.PlatformApp;
import net.atlasauth.atlas.models.RotateOAuthClientSecretBody;
import net.atlasauth.atlas.models.UpdateInstanceBody;
import net.atlasauth.atlas.models.UpdatePlatformAppBody;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * OAuth client secret rotation with overlap, the development email sender, managed email on your
 * own provider account, origin integrations, issuer fields and the new error codes.
 */
class SecretRotationEmailTest {

    private MockBackend mock;
    private AtlasClient atlas;
    private final ObjectMapper om = new ObjectMapper();

    @BeforeEach
    void setUp() throws IOException {
        mock = MockBackend.start();
        atlas = AtlasClient.builder().secretKey("sk_test_123").apiUrl(mock.baseUrl()).build();
    }

    @AfterEach
    void tearDown() {
        mock.close();
    }

    @Test
    void rotateSecretSendsOverlapAndDecodesTheExpiry() throws IOException {
        mock.reply(200, "{\"object\":\"oauth_client\",\"id\":\"oc_1\",\"client_secret\":\"cs_new\","
                + "\"overlap_seconds\":0,\"previous_secret_expires_at\":null}");
        OAuthClientWithSecret out = atlas.oauthClients().rotateSecret("oc_1",
                new RotateOAuthClientSecretBody().overlapSeconds(0), "idem_1");
        assertEquals("POST", mock.method);
        assertEquals("/v1/oauth_clients/oc_1/rotate_secret", mock.path);
        assertEquals(om.readTree("{\"overlap_seconds\":0}"), om.readTree(mock.requestBody));
        assertEquals("idem_1", mock.idempotencyKey);
        assertEquals("cs_new", out.clientSecret());
        assertEquals(0, out.overlapSeconds());
        assertNull(out.previousSecretExpiresAt());

        mock.reply(200, "{\"object\":\"oauth_client\",\"id\":\"oc_1\",\"overlap_seconds\":86400,"
                + "\"previous_secret_expires_at\":1800000000000}");
        OAuthClientWithSecret dflt = atlas.oauthClients().rotateSecret("oc_1");
        assertEquals(1_800_000_000_000L, dflt.previousSecretExpiresAt());
    }

    @Test
    void developmentEmailGetEnableDisable() throws IOException {
        String dev = "{\"object\":\"development_email\",\"provider_id\":\"mp_1\",\"enabled\":true,\"active\":true,"
                + "\"transport\":\"resend\",\"from_address\":\"onboarding@resend.dev\",\"subject_prefix\":\"[Atlas test] \","
                + "\"footer\":\"f\",\"templates\":[\"magic_link\"],\"limits\":{\"daily\":100,\"per_recipient_hourly\":10},"
                + "\"updated_at\":5}";
        mock.reply(200, dev);
        DevelopmentEmail got = atlas.messaging().getDevelopmentEmail();
        assertEquals("GET", mock.method);
        assertEquals("/v1/messaging_providers/email/development", mock.path);
        assertEquals(10, got.limits().get("per_recipient_hourly"));
        assertEquals("[Atlas test] ", got.subjectPrefix());

        mock.reply(201, dev);
        atlas.messaging().enableDevelopmentEmail(new DevelopmentEmailBody().providerId("mp_1"));
        assertEquals("POST", mock.method);
        assertEquals(om.readTree("{\"provider_id\":\"mp_1\"}"), om.readTree(mock.requestBody));

        mock.reply(200, "{\"object\":\"development_email\",\"deleted\":true,\"provider_id\":\"mp_1\",\"mode\":\"standard\"}");
        DevelopmentEmailDisabled gone = atlas.messaging().disableDevelopmentEmail();
        assertEquals("DELETE", mock.method);
        assertEquals("/v1/messaging_providers/email/development", mock.path);
        assertEquals("standard", gone.mode());
    }

    @Test
    void managedEmailOnYourOwnAccountAndProviderMode() throws IOException {
        mock.reply(201, "{\"object\":\"managed_email\",\"backend\":\"brevo\",\"provider_id\":\"mp_2\",\"limits\":null,"
                + "\"reputation\":\"your_provider_account\",\"feedback_webhook\":\"registered\",\"verified\":false,"
                + "\"created_at\":0,\"updated_at\":0}");
        ManagedEmail me = atlas.messaging().createManagedEmail(new ManagedEmailBody()
                .fromAddress("hi@acme.test").transport("brevo").apiKey("xkeysib-x"));
        assertEquals(om.readTree("{\"from_address\":\"hi@acme.test\",\"transport\":\"brevo\","
                + "\"config\":{\"api_key\":\"xkeysib-x\"}}"), om.readTree(mock.requestBody));
        assertEquals("mp_2", me.providerId());
        assertEquals("brevo", me.backend());
        assertEquals("registered", me.feedbackWebhook());
        assertNull(me.limits());

        mock.reply(200, "{\"object\":\"messaging_provider\",\"id\":\"mp_1\",\"channel\":\"email\",\"mode\":\"development\","
                + "\"transport\":\"resend\",\"updated_at\":1}");
        MessagingProvider p = atlas.messaging().setEmail(null, "development", new EmailProviderConfig.Resend("re_x"));
        JsonNode body = om.readTree(mock.requestBody);
        assertEquals("development", body.get("mode").asText());
        assertEquals("resend", body.get("transport").asText());
        assertEquals("mp_1", p.id());
        assertEquals("development", p.mode());
    }

    @Test
    void originEntriesAndIssuerFields() throws IOException {
        mock.reply(200, "{\"object\":\"instance\",\"issuer\":\"https://auth.acme.test\","
                + "\"discovery_url\":\"https://auth.acme.test/.well-known/openid-configuration\","
                + "\"jwks_uri\":\"https://auth.acme.test/.well-known/jwks.json\",\"allowed_origins\":[\"https://app.acme.test\"],"
                + "\"allowed_origin_integrations\":{\"https://app.acme.test\":\"redirect\"},\"created_at\":0}");
        InstanceConfig ins = atlas.instance().get();
        assertEquals("https://auth.acme.test", ins.issuer());
        assertTrue(ins.jwksUri().endsWith("jwks.json"));
        assertEquals("redirect", ins.allowedOriginIntegrations().get("https://app.acme.test"));

        mock.reply(200, "{\"object\":\"instance\",\"allowed_origin_integrations\":{}}");
        atlas.instance().update(new UpdateInstanceBody().allowedOriginEntries(List.of(
                AllowedOriginEntry.of("https://a.acme.test"),
                AllowedOriginEntry.of("https://app.acme.test", AllowedOriginEntry.REDIRECT))));
        assertEquals(om.readTree("{\"allowed_origins\":[\"https://a.acme.test\","
                + "{\"origin\":\"https://app.acme.test\",\"integration\":\"redirect\"}]}"), om.readTree(mock.requestBody));

        String body = Json.mapper().writeValueAsString(new UpdatePlatformAppBody().name("x")
                .allowedOriginEntries(List.of(AllowedOriginEntry.of("https://a.acme.test", AllowedOriginEntry.AUTO))));
        assertEquals(om.readTree("{\"name\":\"x\",\"allowed_origins\":[{\"origin\":\"https://a.acme.test\","
                + "\"integration\":\"auto\"}]}"), om.readTree(body));

        PlatformApp app = Json.mapper().readValue("{\"object\":\"app\",\"frontend_api_host\":\"auth.acme.test\","
                + "\"issuer\":\"https://i\",\"discovery_url\":\"https://d\",\"jwks_uri\":\"https://j\","
                + "\"allowed_origin_integrations\":{\"https://a\":\"embedded\"},\"created_at\":0,\"updated_at\":0}", PlatformApp.class);
        assertEquals("auth.acme.test", app.frontendApiHost());
        assertEquals("embedded", app.allowedOriginIntegrations().get("https://a"));
    }

    @Test
    void loginReadinessVerificationAudiencesAndCodes() throws IOException {
        LoginReadiness lr = Json.mapper().readValue("{\"object\":\"login_readiness\",\"ready\":true,"
                + "\"integration\":{\"assumed\":\"redirect\",\"source\":\"declared\",\"reason\":\"r\"},"
                + "\"checks\":[{\"name\":\"cors\",\"pass\":false,\"applies\":false}]}", LoginReadiness.class);
        assertEquals("redirect", lr.integration().assumed());
        assertFalse(lr.checks().get(0).applies());

        EmailAddress ea = Json.mapper().readValue("{\"verified\":false,"
                + "\"verification\":{\"status\":\"unverified\",\"strategy\":\"skipped\"},\"created_at\":0}", EmailAddress.class);
        assertEquals("skipped", ea.verification().strategy());

        BrandingResponse br = Json.mapper().readValue("{\"object\":\"branding\",\"audiences\":{\"partners\":{\"heading\":\"P\"}},"
                + "\"resolved\":{\"applicationName\":\"A\",\"audiences\":{\"partners\":{\"heading\":\"P\",\"primaryColor\":\"#000\"}}}}",
                BrandingResponse.class);
        assertEquals("P", br.audiences.get("partners").heading());
        assertEquals("#000", br.resolved.audiences().get("partners").primaryColor());

        assertEquals("READ_ONLY_FIELD", ErrorCodes.READ_ONLY_FIELD);
        assertEquals("DEVELOPMENT_EMAIL_NOT_ALLOWED", ErrorCodes.DEVELOPMENT_EMAIL_NOT_ALLOWED);
        assertEquals("DEVELOPMENT_EMAIL_UNAVAILABLE", ErrorCodes.DEVELOPMENT_EMAIL_UNAVAILABLE);
        assertEquals("DEVELOPMENT_EMAIL_LIMIT_REACHED", ErrorCodes.DEVELOPMENT_EMAIL_LIMIT_REACHED);
        assertEquals("DEVELOPMENT_EMAIL_TEMPLATE_NOT_ALLOWED", ErrorCodes.DEVELOPMENT_EMAIL_TEMPLATE_NOT_ALLOWED);
    }
}
