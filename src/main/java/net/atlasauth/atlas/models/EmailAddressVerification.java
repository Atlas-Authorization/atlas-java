package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Whether and how an email address was proven.
 *
 * @param status   {@code verified} or {@code unverified}; mirrors {@code verified}
 * @param strategy {@code email_code}, {@code email_link}, {@code oauth}, {@code admin},
 *                 {@code import}, {@code scim} or {@code lti}; {@code skipped} for an address held
 *                 for sign-in but never proven; null when not verified
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record EmailAddressVerification(String status, String strategy) {
}
