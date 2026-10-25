package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/** The domains an account claimed. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AccountDomainList(String object, List<AccountDomain> data, List<String> verifiedDomains) {
}
