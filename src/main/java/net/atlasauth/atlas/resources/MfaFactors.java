package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.MfaFactorsDuoHealthCheckResponse;
import net.atlasauth.atlas.models.MfaFactorsRemoveDuoSecretResponse;
import net.atlasauth.atlas.models.MfaFactorsRemoveSmartCardSecretResponse;
import net.atlasauth.atlas.models.MfaFactorsRemoveYubicoSecretResponse;
import net.atlasauth.atlas.models.MfaFactorsResetSecurityQuestionsResponse;
import net.atlasauth.atlas.models.MfaFactorsSetDuoSecretBody;
import net.atlasauth.atlas.models.MfaFactorsSetDuoSecretResponse;
import net.atlasauth.atlas.models.MfaFactorsSetSmartCardSecretBody;
import net.atlasauth.atlas.models.MfaFactorsSetSmartCardSecretResponse;
import net.atlasauth.atlas.models.MfaFactorsSetYubicoSecretBody;
import net.atlasauth.atlas.models.MfaFactorsSetYubicoSecretResponse;

import java.util.List;
import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** The {@code /v1/instance} resource. */
public final class MfaFactors {

    private final Transport t;

    public MfaFactors(Transport t) {
        this.t = t;
    }

    /** {@code PUT /v1/instance/yubico_secret} - Store (write-only, encrypted) the Yubico cloud API secret key (base64, from upgrade.yubico.com/getapikey). The client id is mfa.yubikey.clientId. Used when mfa.yubikey.mode is "cloud". Scope: {@code instance:write}. */
    public MfaFactorsSetYubicoSecretResponse setYubicoSecret(MfaFactorsSetYubicoSecretBody body) {
        return t.request("PUT", "/v1/instance/yubico_secret", null, body, null, false, new TypeReference<>() {
        });
    }

    /** {@code DELETE /v1/instance/yubico_secret} - Remove the stored Yubico cloud secret. Cloud-mode YubiKey sign-ins then fail until a new one is stored. Scope: {@code instance:write}. */
    public MfaFactorsRemoveYubicoSecretResponse removeYubicoSecret() {
        return t.request("DELETE", "/v1/instance/yubico_secret", null, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code PUT /v1/instance/duo_secret} - Store (write-only, encrypted) the Duo Web SDK / Universal Prompt client secret. The client id (integration key) and API host are mfa.duo.clientId and mfa.duo.apiHost. Scope: {@code instance:write}. */
    public MfaFactorsSetDuoSecretResponse setDuoSecret(MfaFactorsSetDuoSecretBody body) {
        return t.request("PUT", "/v1/instance/duo_secret", null, body, null, false, new TypeReference<>() {
        });
    }

    /** {@code DELETE /v1/instance/duo_secret} - Remove the stored Duo client secret. Scope: {@code instance:write}. */
    public MfaFactorsRemoveDuoSecretResponse removeDuoSecret() {
        return t.request("DELETE", "/v1/instance/duo_secret", null, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code PUT /v1/instance/smart_card_secret} - Store (write-only, encrypted) the shared secret your TLS proxy sends in the mfa.smartCard.secretHeader header, so Atlas believes the certificate headers only from your proxy. Use a random value of at least 24 characters. Scope: {@code instance:write}. */
    public MfaFactorsSetSmartCardSecretResponse setSmartCardSecret(MfaFactorsSetSmartCardSecretBody body) {
        return t.request("PUT", "/v1/instance/smart_card_secret", null, body, null, false, new TypeReference<>() {
        });
    }

    /** {@code DELETE /v1/instance/smart_card_secret} - Remove the stored proxy secret. Smart card sign-in then refuses every request. Scope: {@code instance:write}. */
    public MfaFactorsRemoveSmartCardSecretResponse removeSmartCardSecret() {
        return t.request("DELETE", "/v1/instance/smart_card_secret", null, null, null, false, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/instance/duo_health_check} - Ask Duo whether the configured integration key, stored secret and API host are accepted (Duo's health check). Reports ok and a message; nothing is changed. Scope: {@code instance:read}. */
    public MfaFactorsDuoHealthCheckResponse duoHealthCheck() {
        return duoHealthCheck(null);
    }

    /** As above, with every optional parameter and an {@code Idempotency-Key} so a retry replays the first response. */
    public MfaFactorsDuoHealthCheckResponse duoHealthCheck(String idempotencyKey) {
        return t.request("POST", "/v1/instance/duo_health_check", null, null, idempotencyKey, false, new TypeReference<>() {
        });
    }

    /** {@code DELETE /v1/users/{id}/security_questions} - Clear a user's security-question answers (and any lockout). They set new ones from their account. Scope: {@code users:write}. */
    public MfaFactorsResetSecurityQuestionsResponse resetSecurityQuestions(String id) {
        return t.request("DELETE", "/v1/users/" + enc(id) + "/security_questions", null, null, null, false, new TypeReference<>() {
        });
    }

    /** The {@code yubikeys} sub-resource. */
    public MfaFactorsYubikeys yubikeys() {
        return new MfaFactorsYubikeys(t);
    }

    /** The {@code smartCardMappings} sub-resource. */
    public MfaFactorsSmartCardMappings smartCardMappings() {
        return new MfaFactorsSmartCardMappings(t);
    }
}
