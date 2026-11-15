package net.atlasauth.atlas.machine;

/**
 * What a machine keeps between runs. The private key never leaves it. The JSON form (snake_case:
 * {@code issuer}, {@code machine_id}, {@code private_key_pem}, {@code public_key_pem}, {@code enrolled_at}) is the
 * same file the other Atlas machine clients write.
 *
 * @param privateKeyPem PKCS#8 PEM of the Ed25519 private key
 * @param publicKeyPem  SPKI PEM of the matching public key (what enrolment registered)
 */
public record MachineIdentity(String issuer, String machineId, String privateKeyPem, String publicKeyPem, long enrolledAt) {
}
