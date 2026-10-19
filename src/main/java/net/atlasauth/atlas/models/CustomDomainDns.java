package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** A custom domain's DNS automation (the API token is never returned). */
@JsonIgnoreProperties(ignoreUnknown = true)
public record CustomDomainDns(
        String provider,
        boolean tokenSet) {
}
