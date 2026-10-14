package net.atlasauth.atlas;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.atlasauth.atlas.models.EmailProviderConfig;
import net.atlasauth.atlas.models.MessagingProviderInput;
import net.atlasauth.atlas.models.SmsProviderConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
}
