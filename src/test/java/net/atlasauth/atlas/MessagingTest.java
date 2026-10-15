package net.atlasauth.atlas;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.atlasauth.atlas.models.DeletedMessagingProvider;
import net.atlasauth.atlas.models.EmailProviderConfig;
import net.atlasauth.atlas.models.MessagingProviderInput;
import net.atlasauth.atlas.models.SmsProviderConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Request-shape tests for the typed messaging provider config. */
class MessagingTest {

    private static final String PROVIDER = """
            {"object":"messaging_provider","channel":"email","transport":"smtp","from_address":null,
             "status":"active","config":{"host":"smtp.acme.test"},"secrets_set":{"password":true},"updated_at":1}
            """;

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
    void typedEmailSendsTransportAndExactKeys() throws IOException {
        mock.reply(200, PROVIDER);
        atlas.messaging().setEmail("Acme <no-reply@acme.test>",
                new EmailProviderConfig.Smtp("smtp.acme.test", 465, true, "u", "p"));

        assertEquals("PUT", mock.method);
        assertEquals("/v1/messaging_providers/email", mock.path);
        JsonNode body = om.readTree(mock.requestBody);
        assertEquals("smtp", body.get("transport").asText());
        assertEquals("Acme <no-reply@acme.test>", body.get("from_address").asText());
        assertEquals(om.readTree("{\"host\":\"smtp.acme.test\",\"port\":465,\"secure\":true,"
                + "\"username\":\"u\",\"password\":\"p\"}"), body.get("config"));
    }

    @Test
    void updateOmitsUnsetKeysSoStoredSecretIsKept() throws IOException {
        mock.reply(200, PROVIDER);
        atlas.messaging().setEmail(null, new EmailProviderConfig.Mailgun(null, null, "eu"));

        JsonNode body = om.readTree(mock.requestBody);
        assertEquals("mailgun", body.get("transport").asText());
        assertFalse(body.has("from_address"));
        assertEquals(om.readTree("{\"region\":\"eu\"}"), body.get("config"));
    }

    @Test
    void typedSmsSendsToSmsPath() throws IOException {
        mock.reply(200, PROVIDER);
        atlas.messaging().setSms("+15551234567",
                new SmsProviderConfig.AwsSns("us-east-1", "AKIA", "secret", null, null));

        assertEquals("/v1/messaging_providers/sms", mock.path);
        JsonNode body = om.readTree(mock.requestBody);
        assertEquals("aws_sns", body.get("transport").asText());
        assertEquals(om.readTree("{\"region\":\"us-east-1\",\"access_key_id\":\"AKIA\","
                + "\"secret_access_key\":\"secret\"}"), body.get("config"));
    }

    @Test
    void mailgunRegionIsChecked() {
        assertThrows(IllegalArgumentException.class, () -> new EmailProviderConfig.Mailgun("k", "d", "ap"));
    }

    @Test
    void untypedInputStillWorks() throws IOException {
        mock.reply(200, PROVIDER);
        atlas.messaging().setEmail(new MessagingProviderInput("resend").config(Map.of("api_key", "re_x")));

        JsonNode body = om.readTree(mock.requestBody);
        assertEquals("resend", body.get("transport").asText());
        assertEquals("re_x", body.get("config").get("api_key").asText());
    }

    private static final String DELETED = "{\"object\":\"messaging_provider\",\"channel\":\"email\",\"deleted\":true}";

    @Test
    void deleteChannelSendsNoQueryByDefault() {
        mock.reply(200, DELETED);
        DeletedMessagingProvider d = atlas.messaging().deleteChannel("email");

        assertEquals("DELETE", mock.method);
        assertEquals("/v1/messaging_providers/email", mock.path);
        assertNull(mock.rawQuery);
        assertEquals("messaging_provider", d.object());
        assertEquals("email", d.channel());
        assertTrue(d.deleted());
    }

    @Test
    void deleteChannelWithoutForceSendsNoQuery() {
        mock.reply(200, DELETED);
        atlas.messaging().deleteChannel("sms", false);

        assertEquals("/v1/messaging_providers/sms", mock.path);
        assertNull(mock.rawQuery);
    }

    @Test
    void deleteChannelWithForceSendsForceTrue() {
        mock.reply(200, DELETED);
        assertTrue(atlas.messaging().deleteChannel("email", true).deleted());

        assertEquals("DELETE", mock.method);
        assertEquals("/v1/messaging_providers/email", mock.path);
        assertEquals("force=true", mock.rawQuery);
    }

    @Test
    void deleteEmailProviderInUseIsAConflictListingFeatures() {
        mock.reply(409, "{\"errors\":[{\"code\":\"EMAIL_PROVIDER_IN_USE\",\"message\":\"in use\","
                + "\"meta\":{\"features\":[\"emailCode\",\"passwordReset\"]}}]}");

        ConflictException ex = assertThrows(ConflictException.class,
                () -> atlas.messaging().deleteChannel("email"));

        assertTrue(ex.hasCode(ErrorCodes.EMAIL_PROVIDER_IN_USE));
        assertEquals(List.of("emailCode", "passwordReset"), ex.errors().get(0).meta().get("features"));
    }

    @Test
    void emailProviderErrorCodes() {
        assertEquals("EMAIL_PROVIDER_IN_USE", ErrorCodes.EMAIL_PROVIDER_IN_USE);
        assertEquals("EMAIL_PROVIDER_REQUIRED", ErrorCodes.EMAIL_PROVIDER_REQUIRED);
    }

    @Test
    void captchaSecretIsBringYourOwnKeys() throws IOException {
        mock.reply(200, "{\"object\":\"captcha_secret\",\"provider\":\"turnstile\",\"set\":true}");
        atlas.instanceSecurity().setCaptchaSecret("s3cret", "turnstile");
        assertEquals("PUT", mock.method);
        assertEquals("/v1/instance/captcha_secret", mock.path);
        JsonNode body = new ObjectMapper().readTree(mock.requestBody);
        assertEquals("turnstile", body.get("provider").asText());
        assertEquals("s3cret", body.get("secret").asText());

        mock.reply(200, "{\"object\":\"captcha_secret\",\"provider\":\"hcaptcha\",\"deleted\":true,\"flows_turned_off\":[\"signUp\"]}");
        JsonNode out = atlas.instanceSecurity().deleteCaptchaSecret("hcaptcha", true);
        assertEquals("DELETE", mock.method);
        assertEquals("provider=hcaptcha&force=true", mock.rawQuery);
        assertEquals("signUp", out.get("flows_turned_off").get(0).asText());

        mock.reply(200, "{\"object\":\"captcha_secret\",\"provider\":\"hcaptcha\",\"deleted\":true}");
        atlas.instanceSecurity().deleteCaptchaSecret();
        assertNull(mock.rawQuery);
    }

    @Test
    void captchaInUseIsAConflict() {
        mock.reply(409, "{\"errors\":[{\"code\":\"CAPTCHA_IN_USE\",\"message\":\"in use\",\"meta\":{\"flows\":[\"signUp\"]}}]}");
        ConflictException ex = assertThrows(ConflictException.class, () -> atlas.instanceSecurity().deleteCaptchaSecret());
        assertTrue(ex.hasCode(ErrorCodes.CAPTCHA_IN_USE));
        assertEquals("CAPTCHA_KEYS_REQUIRED", ErrorCodes.CAPTCHA_KEYS_REQUIRED);
    }
}
