package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * A bulk user import/export job. It runs synchronously for a small batch (201
 * with the finished job) or is queued for a larger one (202 with a {@code pending}
 * job the caller polls). An export never carries a password hash or provider
 * secret, by construction.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ImportExportJob(
        String object,
        String id,
        String type,
        String status,
        int total,
        int processed,
        int succeeded,
        int failed,
        int errorCount,
        /** Present only on an export job: the serialised user array, or null. */
        List<ImportExportExportedUser> result,
        long createdAt,
        long updatedAt,
        Long completedAt) {
}
