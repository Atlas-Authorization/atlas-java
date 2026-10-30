package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** The range a configurable cookie lifetime may take, and the setting that controls it. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record DeclaredCookieLifetimeRange(String setting, Long minSeconds, Long maxSeconds, Long defaultSeconds) {
}
