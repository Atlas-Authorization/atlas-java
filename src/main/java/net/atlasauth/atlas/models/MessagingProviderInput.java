package net.atlasauth.atlas.models;

import java.util.Map;

/** Body for setting a messaging channel's provider. */
public final class MessagingProviderInput {
    /** The transport, e.g. {@code resend}, {@code ses}, {@code smtp} (email) or {@code twilio} (sms). */
    public String transport;
    public String fromAddress;
    /** Transport config; carries the write-only secret keys on write. */
    public Map<String, Object> config;

    public MessagingProviderInput() {
    }

    public MessagingProviderInput(String transport) {
        this.transport = transport;
    }

    public MessagingProviderInput transport(String v) { this.transport = v; return this; }
    public MessagingProviderInput fromAddress(String v) { this.fromAddress = v; return this; }
    public MessagingProviderInput config(Map<String, Object> v) { this.config = v; return this; }
}
