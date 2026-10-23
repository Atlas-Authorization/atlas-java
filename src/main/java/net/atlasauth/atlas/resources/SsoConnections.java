package net.atlasauth.atlas.resources;

import net.atlasauth.atlas.models.SamlEncryptionKey;
import net.atlasauth.atlas.models.UpstreamSigningKey;
import net.atlasauth.atlas.models.ProfileMappingPreview;
import net.atlasauth.atlas.Params;
import java.util.Map;
import com.fasterxml.jackson.core.type.TypeReference;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.CreateSsoConnectionBody;
import net.atlasauth.atlas.models.DeletedObject;
import net.atlasauth.atlas.models.ListPage;
import net.atlasauth.atlas.models.SsoConnection;
import net.atlasauth.atlas.models.SsoConnectionSamlMetadata;
import net.atlasauth.atlas.models.UpdateSsoConnectionBody;

import static net.atlasauth.atlas.http.Transport.enc;

/** The {@code /v1/sso_connections} resource. */
public final class SsoConnections {

    private final Transport t;

    public SsoConnections(Transport t) {
        this.t = t;
    }

    public ListPage<SsoConnection> list() {
        return t.get("/v1/sso_connections", new TypeReference<>() {
        });
    }

    public SsoConnection get(String id) {
        return t.get("/v1/sso_connections/" + enc(id), new TypeReference<>() {
        });
    }

    public SsoConnection create(CreateSsoConnectionBody body) {
        return create(body, null);
    }

    public SsoConnection create(CreateSsoConnectionBody body, String idempotencyKey) {
        return t.post("/v1/sso_connections", body, idempotencyKey, new TypeReference<>() {
        });
    }

    public SsoConnection update(String id, UpdateSsoConnectionBody body) {
        return t.patch("/v1/sso_connections/" + enc(id), body, new TypeReference<>() {
        });
    }

    public DeletedObject delete(String id) {
        return t.delete("/v1/sso_connections/" + enc(id), new TypeReference<>() {
        });
    }

    /** SP SAML metadata for a connection (JSON envelope carrying the XML). */
    public SsoConnectionSamlMetadata samlMetadata(String id) {
        return t.get("/v1/sso_connections/" + enc(id) + "/saml_metadata", new TypeReference<>() {
        });
    }

    /**
     * OIDC: run the connection's profile mapping (or {@code body.profile_mapping}) against a
     * pasted sample ({@code userinfo}, {@code id_token_claims}, {@code id_token}).
     */
    public ProfileMappingPreview previewProfileMapping(String id, Map<String, Object> body) {
        return t.post("/v1/sso_connections/" + enc(id) + "/profile_mapping/preview", body, new TypeReference<>() {
        });
    }

    /** OIDC: the connection's private_key_jwt signing keys. */
    public ListPage<UpstreamSigningKey> listSigningKeys(String id) {
        return t.get("/v1/sso_connections/" + enc(id) + "/signing_keys", new TypeReference<>() {
        });
    }

    /** Generate a new active key ({@code RS256}, {@code PS256} or {@code ES256}; null for the default). */
    public UpstreamSigningKey rotateSigningKey(String id, String alg) {
        return t.post("/v1/sso_connections/" + enc(id) + "/signing_keys", Params.of().set("alg", alg).map(),
                new TypeReference<>() {
                });
    }

    /** Retire a non-active key (it leaves the JWKS). */
    public UpstreamSigningKey retireSigningKey(String id, String kid) {
        return t.post("/v1/sso_connections/" + enc(id) + "/signing_keys/" + enc(kid) + "/retire", null,
                new TypeReference<>() {
                });
    }

    /** The SAML connection's SP encryption certificate(s). */
    public SamlEncryptionKey getSamlEncryptionKey(String id) {
        return t.get("/v1/sso_connections/" + enc(id) + "/saml_encryption_key", new TypeReference<>() {
        });
    }

    /**
     * Generate the SP encryption key pair, or ROTATE it when one exists: the current pair
     * becomes the previous one (still advertised and accepted until
     * {@link #retirePreviousSamlEncryptionKey(String)}).
     */
    public SamlEncryptionKey createSamlEncryptionKey(String id) {
        return t.post("/v1/sso_connections/" + enc(id) + "/saml_encryption_key", null, new TypeReference<>() {
        });
    }

    /** Finish a rotation: stop accepting the previous encryption key. */
    public SamlEncryptionKey retirePreviousSamlEncryptionKey(String id) {
        return t.delete("/v1/sso_connections/" + enc(id) + "/saml_encryption_key/previous", new TypeReference<>() {
        });
    }

    /** Turn assertion encryption off (refused while encrypted assertions are required). */
    public SamlEncryptionKey deleteSamlEncryptionKey(String id) {
        return t.delete("/v1/sso_connections/" + enc(id) + "/saml_encryption_key", new TypeReference<>() {
        });
    }
}
