package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * A Sign in with Atlas test or review account: confined to the app(s) it was made for. {@code password} is present only
 * on create and on a password rotation (returned exactly once); with a secret recipient it arrives as
 * {@code sealedPassword} instead. Neither value appears in {@link #toString()}.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AtlasIdProvisionedAccount(
        String object,
        String id,
        /** The Atlas ID subject the app sees. */ String sub,
        String email,
        boolean emailVerified,
        String label,
        /** provisioned, test_mode or developer. */ String kind,
        /** app or staff. */ String provisionedBy,
        List<String> appIds,
        List<AtlasIdProvisionedAccountApp> apps,
        boolean anyApp,
        boolean twoFactorExempt,
        /** Epoch ms; null = never expires. */ Long expiresAt,
        boolean expired,
        String developerAccountId,
        AtlasIdProvisionedAccountActor createdBy,
        Long lastSignInAt,
        Long passwordRotatedAt,
        long createdAt,
        long updatedAt,
        /** Only on create and password rotation: the password, returned exactly once. */ String password,
        /** With a secret recipient: the password sealed to it (then {@code password} is absent). */ String sealedPassword,
        String note) {

    @Override
    public String toString() {
        return "AtlasIdProvisionedAccount[id=" + id + ", email=" + email + ", label=" + label + ", kind=" + kind
                + ", password=" + (password == null ? "unset" : "***")
                + ", sealedPassword=" + (sealedPassword == null ? "unset" : "***") + "]";
    }
}
