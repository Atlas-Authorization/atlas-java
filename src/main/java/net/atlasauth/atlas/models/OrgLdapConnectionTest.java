package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OrgLdapConnectionTest(
        String object,
        String id,
        Boolean ok,
        String error) {
}
