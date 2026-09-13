package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Map;

/** The {@code GET /v1/domains} envelope: the domains plus setup guidance. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ListDomainsResponse(
        String object,
        List<CustomDomain> data,
        String cnameTarget,
        Map<String, Object> instructions,
        List<DomainCookieCheck> cookieChecks,
        boolean required,
        String environment) {
}
