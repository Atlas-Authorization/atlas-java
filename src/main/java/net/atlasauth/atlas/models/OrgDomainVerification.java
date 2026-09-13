package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OrgDomainVerification(
        String recordName,
        String recordType,
        String recordValue) {
}
