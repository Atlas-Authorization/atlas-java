package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code POST /v1/yubikeys}. Required: publicId, aesKey. */
public final class MfaFactorsYubikeysRegisterBody {
    /** Modhex prefix printed on the key (2 to 16 characters). Required. */
    public String publicId;
    /** 32 hex characters. Required. */
    public String aesKey;
    /** Optional 12 hex characters; when given the decrypted token must carry it. */
    public String privateId;
    public String label;
    public String userId;

    public MfaFactorsYubikeysRegisterBody() {
    }

    public MfaFactorsYubikeysRegisterBody publicId(String v) { this.publicId = v; return this; }
    public MfaFactorsYubikeysRegisterBody aesKey(String v) { this.aesKey = v; return this; }
    public MfaFactorsYubikeysRegisterBody privateId(String v) { this.privateId = v; return this; }
    public MfaFactorsYubikeysRegisterBody label(String v) { this.label = v; return this; }
    public MfaFactorsYubikeysRegisterBody userId(String v) { this.userId = v; return this; }
}
