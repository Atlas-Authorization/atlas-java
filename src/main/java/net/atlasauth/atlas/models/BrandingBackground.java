package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

/** The hosted-page background bag ({@code solid} | {@code gradient} | {@code image} | {@code wall}). */
public final class BrandingBackground {
    public String type;
    public String color;
    public String from;
    public String to;
    public Integer angle;
    @JsonProperty("imageUrl")
    public String imageUrl;

    public BrandingBackground() {
    }

    public BrandingBackground(String type) {
        this.type = type;
    }

    public BrandingBackground type(String v) { this.type = v; return this; }
    public BrandingBackground color(String v) { this.color = v; return this; }
    public BrandingBackground from(String v) { this.from = v; return this; }
    public BrandingBackground to(String v) { this.to = v; return this; }
    public BrandingBackground angle(Integer v) { this.angle = v; return this; }
    public BrandingBackground imageUrl(String v) { this.imageUrl = v; return this; }
}
