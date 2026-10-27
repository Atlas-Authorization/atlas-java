package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code POST /v1/vc/presentation_requests}. Required: vct. */
public final class VcPresentationRequestsCreateBody {
    /** The vct, or the docType for mso_mdoc. Required. */
    public String vct;
    /** Default dc+sd-jwt. */
    public String format;
    /** mso_mdoc only: PEM IACA roots for this request; overrides the credential type's. Certificate chain mode only: no bare key is accepted when roots apply. */
    public List<String> iacaRoots;
    /** Claims the wallet must disclose. */
    public List<String> claims;
    /** Other issuers accepted besides this instance. */
    public List<Map<String, Object>> trustedIssuers;
    /** Default 600. */
    public Long expiresInSeconds;

    public VcPresentationRequestsCreateBody() {
    }

    public VcPresentationRequestsCreateBody vct(String v) { this.vct = v; return this; }
    public VcPresentationRequestsCreateBody format(String v) { this.format = v; return this; }
    public VcPresentationRequestsCreateBody iacaRoots(List<String> v) { this.iacaRoots = v; return this; }
    public VcPresentationRequestsCreateBody claims(List<String> v) { this.claims = v; return this; }
    public VcPresentationRequestsCreateBody trustedIssuers(List<Map<String, Object>> v) { this.trustedIssuers = v; return this; }
    public VcPresentationRequestsCreateBody expiresInSeconds(Long v) { this.expiresInSeconds = v; return this; }
}
