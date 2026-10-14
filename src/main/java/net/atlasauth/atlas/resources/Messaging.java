package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.DeletedMessagingProvider;
import net.atlasauth.atlas.models.EmailProviderConfig;
import net.atlasauth.atlas.models.SmsProviderConfig;
import net.atlasauth.atlas.models.MessagingProvider;
import net.atlasauth.atlas.models.MessagingProviderInput;
import net.atlasauth.atlas.models.MessagingProviders;
import net.atlasauth.atlas.models.MessagingTestResult;

import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** The {@code /v1/messaging_providers} resource. */
public final class Messaging {

    private final Transport t;

    public Messaging(Transport t) {
        this.t = t;
    }

    /** The instance's configured email and SMS providers, secrets omitted. */
    public MessagingProviders get() {
        return t.get("/v1/messaging_providers", new TypeReference<>() {
        });
    }

    /**
     * Configure (or update) the email provider with typed, per-transport settings.
     * On an update of the same transport, null components keep their stored values
     * (including stored secrets).
     *
     * @param fromAddress the From address, e.g. {@code "Acme <no-reply@acme.com>"}; may be null
     * @param config      the transport's settings, e.g. {@code new EmailProviderConfig.Resend("re_…")}
     */
    public MessagingProvider setEmail(String fromAddress, EmailProviderConfig config) {
        return setEmail(new MessagingProviderInput(config.transport())
                .fromAddress(fromAddress)
                .config(config.toConfig()));
    }

    /**
     * Configure (or update) the SMS provider with typed, per-transport settings.
     * On an update of the same transport, null components keep their stored values
     * (including stored secrets).
     *
     * @param fromAddress the sender number in E.164 format, e.g. {@code +15551234567}; may be null
     * @param config      the transport's settings, e.g. {@code new SmsProviderConfig.Twilio(...)}
     */
    public MessagingProvider setSms(String fromAddress, SmsProviderConfig config) {
        return setSms(new MessagingProviderInput(config.transport())
                .fromAddress(fromAddress)
                .config(config.toConfig()));
    }

    /** Set the email provider from an untyped body. Prefer {@link #setEmail(String, EmailProviderConfig)}. */
    public MessagingProvider setEmail(MessagingProviderInput body) {
        return t.put("/v1/messaging_providers/email", body, new TypeReference<>() {
        });
    }

    /** Set the SMS provider from an untyped body. Prefer {@link #setSms(String, SmsProviderConfig)}. */
    public MessagingProvider setSms(MessagingProviderInput body) {
        return t.put("/v1/messaging_providers/sms", body, new TypeReference<>() {
        });
    }

    public DeletedMessagingProvider deleteChannel(String channel) {
        return t.delete("/v1/messaging_providers/" + enc(channel), new TypeReference<>() {
        });
    }

    /** Send a fixed, clearly-marked test message through the channel's provider. */
    public MessagingTestResult test(String channel, String to) {
        Map<String, Object> body = Params.of("to", to).map();
        return t.post("/v1/messaging_providers/" + enc(channel) + "/test", body, new TypeReference<>() {
        });
    }
}
