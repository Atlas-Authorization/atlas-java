package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** What a credential is scoped to: {@code type} is instance, account, application or webhook_endpoint. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ResourceRef(String type, String id) {
}
