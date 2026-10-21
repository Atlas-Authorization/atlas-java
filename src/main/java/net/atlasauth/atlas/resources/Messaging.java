package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.ManagedEmail;
import net.atlasauth.atlas.models.ManagedEmailBody;
import net.atlasauth.atlas.models.ManagedEmailDeleted;
import net.atlasauth.atlas.models.DeletedMessagingProvider;
import net.atlasauth.atlas.models.DevelopmentEmail;
import net.atlasauth.atlas.models.DevelopmentEmailBody;
import net.atlasauth.atlas.models.DevelopmentEmailDisabled;
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

    /** Remove the channel's provider. See {@link #deleteChannel(String, boolean)}. */
    public DeletedMessagingProvider deleteChannel(String channel) {
        return deleteChannel(channel, false);
    }

    /**
     * Remove the channel's provider. Removing the email provider while nothing else
     * would deliver email is refused with 409 {@code EMAIL_PROVIDER_IN_USE}; the error's
     * {@code meta.features} lists the email-dependent features that are on (e.g.
     * {@code emailCode}, {@code emailLink}, {@code emailVerification}, {@code passwordReset}).
     *
     * @param force true sends {@code ?force=true} to remove the email provider anyway
     */
    public DeletedMessagingProvider deleteChannel(String channel, boolean force) {
        Map<String, Object> q = Params.of().set("force", force ? Boolean.TRUE : null).map();
        return t.delete("/v1/messaging_providers/" + enc(channel), q, new TypeReference<>() {
        });
    }

    /**
     * Configure (or update) the email provider with typed settings and a mode:
     * {@code development} lets a development instance send without a verified domain (409 on a
     * production instance); {@code standard} sends normally; null keeps the stored mode.
     */
    public MessagingProvider setEmail(String fromAddress, String mode, EmailProviderConfig config) {
        return setEmail(new MessagingProviderInput(config.transport())
                .fromAddress(fromAddress)
                .mode(mode)
                .config(config.toConfig()));
    }

    /** {@code GET /v1/messaging_providers/email/development} — The email provider in development mode (404 when it is not). */
    public DevelopmentEmail getDevelopmentEmail() {
        return t.get("/v1/messaging_providers/email/development", new TypeReference<>() {
        });
    }

    /**
     * {@code POST /v1/messaging_providers/email/development} — Put your own email provider in
     * development mode, so a development instance sends Atlas's sign-in emails without a verified
     * domain ({@code [Atlas test] } subjects, capped). 409 {@code DEVELOPMENT_EMAIL_NOT_ALLOWED} on a
     * production instance.
     */
    public DevelopmentEmail enableDevelopmentEmail(DevelopmentEmailBody body) {
        return t.post("/v1/messaging_providers/email/development", body, new TypeReference<>() {
        });
    }

    /** {@code DELETE /v1/messaging_providers/email/development} — Back to standard mode (the provider and its key stay). */
    public DevelopmentEmailDisabled disableDevelopmentEmail() {
        return t.delete("/v1/messaging_providers/email/development", new TypeReference<>() {
        });
    }

    /** {@code GET /v1/messaging_providers/email/managed} — The managed sending domain (404 when none). */
    public ManagedEmail getManagedEmail() {
        return t.get("/v1/messaging_providers/email/managed", new TypeReference<>() {
        });
    }

    /**
     * {@code POST /v1/messaging_providers/email/managed} — Set your sending domain up on your own
     * Resend or Brevo account ({@code transport} + {@code apiKey}, or {@code providerId}); returns the
     * DNS records to publish.
     */
    public ManagedEmail createManagedEmail(ManagedEmailBody body) {
        return t.post("/v1/messaging_providers/email/managed", body, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/messaging_providers/email/managed/verify} — Re-check the DNS records now. */
    public ManagedEmail verifyManagedEmail() {
        return t.post("/v1/messaging_providers/email/managed/verify", null, new TypeReference<>() {
        });
    }

    public ManagedEmailDeleted deleteManagedEmail() {
        return deleteManagedEmail(false);
    }

    /**
     * {@code DELETE /v1/messaging_providers/email/managed} — Stop using managed email (DNS records stay).
     * Refused with 409 {@code EMAIL_PROVIDER_IN_USE} while it is the only way to send email.
     *
     * @param force true sends {@code ?force=true} to remove it anyway
     */
    public ManagedEmailDeleted deleteManagedEmail(boolean force) {
        Map<String, Object> q = Params.of().set("force", force ? Boolean.TRUE : null).map();
        return t.delete("/v1/messaging_providers/email/managed", q, new TypeReference<>() {
        });
    }

    /** Send a fixed, clearly-marked test message through the channel's provider. */
    public MessagingTestResult test(String channel, String to) {
        Map<String, Object> body = Params.of("to", to).map();
        return t.post("/v1/messaging_providers/" + enc(channel) + "/test", body, new TypeReference<>() {
        });
    }
}
