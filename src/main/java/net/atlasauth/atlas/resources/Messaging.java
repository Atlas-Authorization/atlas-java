package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.DeletedMessagingProvider;
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

    public MessagingProvider setEmail(MessagingProviderInput body) {
        return t.put("/v1/messaging_providers/email", body, new TypeReference<>() {
        });
    }

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
