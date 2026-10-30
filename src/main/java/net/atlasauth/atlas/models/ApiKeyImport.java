package net.atlasauth.atlas.models;

/**
 * Import an existing key by its hash instead of minting one ({@link CreateApiKeyBody#importKey}): the response
 * has no secret, and the original secret verifies from then on.
 *
 * @param secretHash    the hash of the full secret
 * @param hashAlgorithm {@code sha256} or {@code sha256_hex}; null for the server default
 * @param prefix        the key's prefix
 */
public record ApiKeyImport(String secretHash, String hashAlgorithm, String prefix) {
    public ApiKeyImport(String secretHash, String prefix) {
        this(secretHash, null, prefix);
    }
}
