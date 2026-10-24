package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PlatformSecretKey(
        String object,
        String id,
        String prefix,
        long createdAt,
        Long expiresAt,
        String name,
        /** Provisioning tags (strings, numbers and booleans). */ Map<String, Object> metadata,
        /** Your idempotent-create key. */ String externalRef,
        /** True on a create that sent an {@code external_ref} and got the existing object back (200, no secret). */ Boolean idempotentReplay,
        String appId,
        String instanceId,
        /** The key, shown once on a fresh create (never on an idempotent replay). */ String secret,
        String note) {
    /** The shape before the name, provisioning and create fields. */
    public PlatformSecretKey(String object, String id, String prefix, long createdAt, Long expiresAt) {
        this(object, id, prefix, createdAt, expiresAt, null, null, null, null, null, null, null, null);
    }

}
