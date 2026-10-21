package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** The instance's configured email and SMS providers, secrets omitted. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record MessagingProviders(
        String object,
        MessagingProvider email,
        MessagingProvider sms,
        /** The Atlas-managed sending domain, or null. */ ManagedEmailSummary managedEmail,
        /** The email provider in development mode, or null. */ DevelopmentEmailSummary developmentEmail) {
    /** The 1.4 shape, without {@code developmentEmail}. */
    public MessagingProviders(String object, MessagingProvider email, MessagingProvider sms, ManagedEmailSummary managedEmail) {
        this(object, email, sms, managedEmail, null);
    }

    /** The previous shape, without {@code managedEmail}. */
    public MessagingProviders(String object, MessagingProvider email, MessagingProvider sms) {
        this(object, email, sms, null);
    }
}
