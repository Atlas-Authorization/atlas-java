package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** SP SAML metadata for a connection — a JSON envelope carrying the XML. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record SsoConnectionSamlMetadata(
        String object,
        String spEntityId,
        String acsUrl,
        /** The SP EntityDescriptor XML, carried as a string inside this JSON envelope. */
        String metadataXml) {
}
