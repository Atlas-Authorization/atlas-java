package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** A single origin's first-party cookie check within {@link ListDomainsResponse}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record DomainCookieCheck(
        String origin,
        boolean firstParty) {
}
