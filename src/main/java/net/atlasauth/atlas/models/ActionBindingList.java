package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/** The ordered binding list for a trigger, replaced atomically as a set. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ActionBindingList(
        String object,
        String trigger,
        List<String> actionIds) {
}
