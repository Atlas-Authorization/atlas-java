package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** A response object returned by the Backend API. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record UsersSearchQueryResponseDataItem(
        String object,
        String id,
        String username,
        String firstName,
        String lastName,
        String imageUrl,
        List<Map<String, Object>> emailAddresses,
        String primaryEmailAddressId,
        String primaryEmail,
        String externalId,
        Boolean passwordEnabled,
        Map<String, Object> publicMetadata,
        Boolean mfaEnabled,
        List<String> requiredFactors,
        Boolean isAnonymous,
        Boolean banned,
        String banReason,
        String banNote,
        Boolean locked,
        Long deletedAt,
        Long lastSignInAt,
        Long createdAt,
        Long updatedAt,
        Long version,
        Object referral,
        String referralCode,
        String referralLink) {
}
