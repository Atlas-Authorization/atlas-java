package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** {@code POST /v1/instance/referrals/calls/retry_failed}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ReferralRetryFailedResult(String object, /** How many failed calls were queued to send now. */ int requeued) {
}
