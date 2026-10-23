package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** SP SAML metadata for a connection — a JSON envelope carrying the XML. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record SsoConnectionSamlMetadata(
        String object,
        String spEntityId,
        String acsUrl,
        /** The SP EntityDescriptor XML, carried as a string inside this JSON envelope. */
        String metadataXml,
        /** Our SingleLogoutService URL (both bindings), when Single Logout is on. */ String sloUrl) {

    /** The 1.11 shape, before the custom-data, add-on, enterprise and token-format fields. */
    public SsoConnectionSamlMetadata(String object, String spEntityId, String acsUrl, String metadataXml) {
        this(object, spEntityId, acsUrl, metadataXml, null);
    }
}
