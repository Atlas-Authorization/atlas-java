package net.atlasauth.atlas.models;

import java.util.Map;

/**
 * Typed settings for an email provider, one record per transport. Each record
 * produces exactly the {@code config} keys the API accepts for that transport.
 *
 * <p>Pass {@code null} for any component you are not setting; it is left out of
 * the request. Components documented as "Required on first setup" must be set the
 * first time you configure that transport (or switch to it). On an update of the
 * SAME transport, an omitted key keeps its stored value — including a stored
 * secret — so a from-address change need not re-enter the API key.
 *
 * <p>Secrets are write-only: they are never returned. A response reports only
 * which secret keys are set ({@link MessagingProvider#secretsSet()}).
 */
public sealed interface EmailProviderConfig
        permits EmailProviderConfig.Resend, EmailProviderConfig.Ses, EmailProviderConfig.Smtp,
        EmailProviderConfig.Brevo, EmailProviderConfig.Mailgun {

    /** The transport name sent as {@code transport}. */
    String transport();

    /** The exact {@code config} object for this transport (null components omitted). */
    Map<String, Object> toConfig();

    /**
     * Resend.
     *
     * @param apiKey Resend API key ({@code re_…}). Required on first setup. Write-only.
     */
    record Resend(String apiKey) implements EmailProviderConfig {
        public String transport() { return "resend"; }
        public Map<String, Object> toConfig() { return ProviderConfigs.of("api_key", apiKey); }
    }

    /**
     * Amazon SES.
     *
     * @param region          AWS region, e.g. {@code us-east-1}. Required on first setup.
     * @param accessKeyId     IAM access key ID. Required on first setup. Write-only.
     * @param secretAccessKey IAM secret access key. Required on first setup. Write-only.
     * @param sessionToken    STS session token, only for temporary credentials. Write-only.
     */
    record Ses(String region, String accessKeyId, String secretAccessKey, String sessionToken)
            implements EmailProviderConfig {
        public String transport() { return "ses"; }
        public Map<String, Object> toConfig() {
            return ProviderConfigs.of("region", region, "access_key_id", accessKeyId,
                    "secret_access_key", secretAccessKey, "session_token", sessionToken);
        }
    }

    /**
     * Any SMTP server.
     *
     * @param host     SMTP server hostname, e.g. {@code smtp.example.com}. Required on first setup.
     * @param port     SMTP port (1-65535). Default 587.
     * @param secure   Use implicit TLS. Default: true on port 465, otherwise STARTTLS.
     * @param username SMTP username, if the server requires authentication.
     * @param password SMTP password or app password. Write-only.
     */
    record Smtp(String host, Integer port, Boolean secure, String username, String password)
            implements EmailProviderConfig {
        public String transport() { return "smtp"; }
        public Map<String, Object> toConfig() {
            return ProviderConfigs.of("host", host, "port", port, "secure", secure,
                    "username", username, "password", password);
        }
    }

    /**
     * Brevo.
     *
     * @param apiKey Brevo API key ({@code xkeysib-…}). Required on first setup. Write-only.
     */
    record Brevo(String apiKey) implements EmailProviderConfig {
        public String transport() { return "brevo"; }
        public Map<String, Object> toConfig() { return ProviderConfigs.of("api_key", apiKey); }
    }

    /**
     * Mailgun.
     *
     * @param apiKey Mailgun private API key. Required on first setup. Write-only.
     * @param domain Your Mailgun sending domain, e.g. {@code mg.example.com}. Required on first setup.
     * @param region Mailgun region, {@code us} or {@code eu}. Default {@code us}.
     */
    record Mailgun(String apiKey, String domain, String region) implements EmailProviderConfig {
        public Mailgun {
            if (region != null && !region.isEmpty() && !region.equals("us") && !region.equals("eu")) {
                throw new IllegalArgumentException("Mailgun region must be us or eu");
            }
        }
        public String transport() { return "mailgun"; }
        public Map<String, Object> toConfig() {
            return ProviderConfigs.of("api_key", apiKey, "domain", domain, "region", region);
        }
    }
}
