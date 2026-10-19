package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** The DNS TXT record that proves the publisher domain. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AtlasIdPublisherVerification(
        String recordName,
        String recordType,
        String recordValue) {
}
