package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** The instance's configured email and SMS providers, secrets omitted. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record MessagingProviders(
        String object,
        MessagingProvider email,
        MessagingProvider sms,
        /** The Atlas-managed sending domain, or null. */ ManagedEmailSummary managedEmail) {

    /** The previous shape, without {@code managedEmail}. */
    public MessagingProviders(String object, MessagingProvider email, MessagingProvider sms) {
        this(object, email, sms, null);
    }
}
