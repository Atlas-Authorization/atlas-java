package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** The DNS TXT record that proves a claimed domain. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AccountDomainVerification(String type, String name, String value) {
}
