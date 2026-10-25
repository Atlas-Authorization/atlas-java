package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/** What to send a partner so it can be registered: the receiver URL, the signature it must use and where to send it. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record PartnerPlatformRegistration(
        String object,
        String partner,
        String platformId,
        String receiverUrl,
        boolean receiverUrlIsTemplate,
        String externalId,
        List<String> events,
        Signature signature,
        SendTo sendTo,
        String tokenPrefix,
        String nextStep) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Signature(String header, String scheme, String format, String jwksUrl, int toleranceSeconds) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record SendTo(String contact, String docs) {
    }
}
