package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** A user's acceptance of a policy version, with the reading evidence. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record PolicyAcceptance(
        String object,
        String id,
        String userId,
        String policy,
        String version,
        String policyVersionId,
        String status,
        String source,
        String contentSha256,
        String display,
        Boolean scrolledToEnd,
        Integer viewMs,
        String locale,
        String ip,
        String userAgent,
        Long acceptedAt) {
}
