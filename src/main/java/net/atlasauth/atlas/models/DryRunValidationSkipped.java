package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** Why a plan-mode dry run did not validate to the end: {@code reason} is external_call, database or unsupported_response. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record DryRunValidationSkipped(String reason, String detail) {
}
