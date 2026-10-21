package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PlatformInstanceSecret(
        String object,
        /** Deprecated: never sent by the API; read {@link #instanceId()}. */ String id,
        /** The new secret key, revealed exactly once. */ String secret,
        String note,
        String appId,
        String instanceId,
        /** The overlap applied, in seconds. */ Long overlapSeconds,
        /** When the prior keys stop working (epoch ms); null on a hard cut. */ Long previousKeysExpireAt) {

    /** The 1.5 shape, without {@code appId}, {@code instanceId}, {@code overlapSeconds}, {@code previousKeysExpireAt}. */
    public PlatformInstanceSecret(String object, String id, String secret, String note) {
        this(object, id, secret, note, null, null, null, null);
    }
}
