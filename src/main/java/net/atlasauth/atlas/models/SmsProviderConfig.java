package net.atlasauth.atlas.models;

import java.util.Map;

/**
 * Typed settings for an SMS provider, one record per transport. Each record
 * produces exactly the {@code config} keys the API accepts for that transport.
 *
 * <p>Pass {@code null} for any component you are not setting; it is left out of
 * the request. Components documented as "Required on first setup" must be set the
 * first time you configure that transport (or switch to it). On an update of the
 * SAME transport, an omitted key keeps its stored value — including a stored
 * secret.
 *
 * <p>Secrets are write-only: they are never returned. A response reports only
 * which secret keys are set ({@link MessagingProvider#secretsSet()}). The optional
 * {@code from} component is the sender number or ID; {@code fromAddress} on the
 * request takes precedence.
 */
public sealed interface SmsProviderConfig
        permits SmsProviderConfig.Twilio, SmsProviderConfig.Vonage, SmsProviderConfig.Messagebird,
        SmsProviderConfig.Plivo, SmsProviderConfig.Telnyx, SmsProviderConfig.Sinch,
        SmsProviderConfig.Infobip, SmsProviderConfig.Clicksend, SmsProviderConfig.Bandwidth,
        SmsProviderConfig.AwsSns {

    /** The transport name sent as {@code transport}. */
    String transport();

    /** The exact {@code config} object for this transport (null components omitted). */
    Map<String, Object> toConfig();

    /**
     * Twilio.
     *
     * @param accountSid   Twilio Account SID ({@code AC…}). Required on first setup.
     * @param authToken    Twilio auth token. Required on first setup. Write-only.
     * @param from         Sender number or ID.
     * @param whatsappFrom WhatsApp-enabled sender number, for the WhatsApp channel.
     * @param voiceFrom    Voice-capable number, for the voice channel.
     */
    record Twilio(String accountSid, String authToken, String from, String whatsappFrom, String voiceFrom)
            implements SmsProviderConfig {
        public String transport() { return "twilio"; }
        public Map<String, Object> toConfig() {
            return ProviderConfigs.of("account_sid", accountSid, "auth_token", authToken, "from", from,
                    "whatsapp_from", whatsappFrom, "voice_from", voiceFrom);
        }
    }

    /**
     * Vonage.
     *
     * @param apiKey    Vonage API key. Required on first setup.
     * @param apiSecret Vonage API secret. Required on first setup. Write-only.
     * @param from      Sender number or ID.
     */
    record Vonage(String apiKey, String apiSecret, String from) implements SmsProviderConfig {
        public String transport() { return "vonage"; }
        public Map<String, Object> toConfig() {
            return ProviderConfigs.of("api_key", apiKey, "api_secret", apiSecret, "from", from);
        }
    }

    /**
     * MessageBird (Bird).
     *
     * @param accessKey MessageBird access key. Required on first setup. Write-only.
     * @param from      Sender number or ID.
     */
    record Messagebird(String accessKey, String from) implements SmsProviderConfig {
        public String transport() { return "messagebird"; }
        public Map<String, Object> toConfig() { return ProviderConfigs.of("access_key", accessKey, "from", from); }
    }

    /**
     * Plivo.
     *
     * @param authId    Plivo Auth ID. Required on first setup.
     * @param authToken Plivo auth token. Required on first setup. Write-only.
     * @param from      Sender number or ID.
     */
    record Plivo(String authId, String authToken, String from) implements SmsProviderConfig {
        public String transport() { return "plivo"; }
        public Map<String, Object> toConfig() {
            return ProviderConfigs.of("auth_id", authId, "auth_token", authToken, "from", from);
        }
    }

    /**
     * Telnyx.
     *
     * @param apiKey Telnyx API key. Required on first setup. Write-only.
     * @param from   Sender number or ID.
     */
    record Telnyx(String apiKey, String from) implements SmsProviderConfig {
        public String transport() { return "telnyx"; }
        public Map<String, Object> toConfig() { return ProviderConfigs.of("api_key", apiKey, "from", from); }
    }

    /**
     * Sinch.
     *
     * @param servicePlanId Sinch service plan ID. Required on first setup.
     * @param apiToken      Sinch API token. Required on first setup. Write-only.
     * @param region        Sinch API region, a short lowercase code such as {@code us} or {@code eu}. Default {@code us}.
     * @param from          Sender number or ID.
     */
    record Sinch(String servicePlanId, String apiToken, String region, String from) implements SmsProviderConfig {
        public String transport() { return "sinch"; }
        public Map<String, Object> toConfig() {
            return ProviderConfigs.of("service_plan_id", servicePlanId, "api_token", apiToken,
                    "region", region, "from", from);
        }
    }

    /**
     * Infobip.
     *
     * @param baseUrl Your Infobip base URL (https), e.g. {@code https://xxxxx.api.infobip.com}. Required on first setup.
     * @param apiKey  Infobip API key. Required on first setup. Write-only.
     * @param from    Sender number or ID.
     */
    record Infobip(String baseUrl, String apiKey, String from) implements SmsProviderConfig {
        public String transport() { return "infobip"; }
        public Map<String, Object> toConfig() {
            return ProviderConfigs.of("base_url", baseUrl, "api_key", apiKey, "from", from);
        }
    }

    /**
     * ClickSend.
     *
     * @param username ClickSend username (your login email). Required on first setup.
     * @param apiKey   ClickSend API key. Required on first setup. Write-only.
     * @param from     Sender number or ID.
     */
    record Clicksend(String username, String apiKey, String from) implements SmsProviderConfig {
        public String transport() { return "clicksend"; }
        public Map<String, Object> toConfig() {
            return ProviderConfigs.of("username", username, "api_key", apiKey, "from", from);
        }
    }

    /**
     * Bandwidth.
     *
     * @param accountId     Bandwidth account ID. Required on first setup.
     * @param applicationId Bandwidth messaging application ID. Required on first setup.
     * @param username      Bandwidth API username. Required on first setup.
     * @param password      Bandwidth API password. Required on first setup. Write-only.
     * @param from          Sender number or ID.
     */
    record Bandwidth(String accountId, String applicationId, String username, String password, String from)
            implements SmsProviderConfig {
        public String transport() { return "bandwidth"; }
        public Map<String, Object> toConfig() {
            return ProviderConfigs.of("account_id", accountId, "application_id", applicationId,
                    "username", username, "password", password, "from", from);
        }
    }

    /**
     * Amazon SNS.
     *
     * @param region          AWS region, e.g. {@code us-east-1}. Required on first setup.
     * @param accessKeyId     IAM access key ID. Required on first setup.
     * @param secretAccessKey IAM secret access key. Required on first setup. Write-only.
     * @param sessionToken    STS session token, only for temporary credentials. Write-only.
     * @param from            Sender number or ID.
     */
    record AwsSns(String region, String accessKeyId, String secretAccessKey, String sessionToken, String from)
            implements SmsProviderConfig {
        public String transport() { return "aws_sns"; }
        public Map<String, Object> toConfig() {
            return ProviderConfigs.of("region", region, "access_key_id", accessKeyId,
                    "secret_access_key", secretAccessKey, "session_token", sessionToken, "from", from);
        }
    }
}
