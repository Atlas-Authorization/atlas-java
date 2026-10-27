package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code POST /v1/vc/credential_types}. Required: vct, name, claims. */
public final class VcCredentialTypesCreateBody {
    /** The credential type identifier (a URN or URL); for mso_mdoc the docType, for example org.iso.18013.5.1.mDL. Required. */
    public String vct;
    /** Default dc+sd-jwt. mso_mdoc issues an ISO 18013-5 mdoc (ES256, holder key P-256). */
    public String format;
    /** mso_mdoc only: the namespace the claims live in (default the docType). */
    public String namespace;
    /** mso_mdoc only: PEM IACA root certificates (ISO 18013-5 Annex B). With roots set, a presentation must carry an x5chain leading to one of them and the issuer signature must verify under its Document Signer key; bare trusted keys are then not accepted. */
    public List<String> iacaRoots;
    /** mso_mdoc only: PEM Document Signer certificate first, then intermediates, published as x5chain. The first certificate must be for this instance's mdoc signing key (the ES256 key in its key set), otherwise 400. */
    public List<String> signerChain;
    /** Required. */
    public String name;
    /** Required. */
    public List<Map<String, Object>> claims;
    /** Default one year. */
    public Long lifetimeSeconds;
    public Boolean requireWalletAttestation;
    /** JWK Set of accepted wallet providers; required with require_wallet_attestation. */
    public Map<String, Object> walletAttestationJwks;

    public VcCredentialTypesCreateBody() {
    }

    public VcCredentialTypesCreateBody vct(String v) { this.vct = v; return this; }
    public VcCredentialTypesCreateBody format(String v) { this.format = v; return this; }
    public VcCredentialTypesCreateBody namespace(String v) { this.namespace = v; return this; }
    public VcCredentialTypesCreateBody iacaRoots(List<String> v) { this.iacaRoots = v; return this; }
    public VcCredentialTypesCreateBody signerChain(List<String> v) { this.signerChain = v; return this; }
    public VcCredentialTypesCreateBody name(String v) { this.name = v; return this; }
    public VcCredentialTypesCreateBody claims(List<Map<String, Object>> v) { this.claims = v; return this; }
    public VcCredentialTypesCreateBody lifetimeSeconds(Long v) { this.lifetimeSeconds = v; return this; }
    public VcCredentialTypesCreateBody requireWalletAttestation(Boolean v) { this.requireWalletAttestation = v; return this; }
    public VcCredentialTypesCreateBody walletAttestationJwks(Map<String, Object> v) { this.walletAttestationJwks = v; return this; }
}
