package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** The connectivity/auth probe's outcome (GET ServiceProviderConfig; writes nothing). */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ScimProvisioningTestResult(
        String object,
        String id,
        boolean ok,
        Integer status,
        String error) {
}
