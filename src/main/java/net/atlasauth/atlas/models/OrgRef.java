package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** A lightweight organization reference used in hierarchy responses. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record OrgRef(String id, String name, String slug) {
}
