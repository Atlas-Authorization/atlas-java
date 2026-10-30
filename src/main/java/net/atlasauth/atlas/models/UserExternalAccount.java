package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** A provider identity linked to a user ({@link User#externalAccounts()}). Tokens are never included. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record UserExternalAccount(
        /** {@code external_account}. */ String object,
        String id,
        String provider,
        String providerUserId,
        /** The provider's address at the last sign-in or import: a snapshot, not proof of ownership. */ String providerEmail,
        boolean emailVerified,
        String firstName,
        String lastName,
        /** The provider's avatar for this identity. */ String imageUrl,
        /** Epoch ms. */ long connectedAt) {
}
