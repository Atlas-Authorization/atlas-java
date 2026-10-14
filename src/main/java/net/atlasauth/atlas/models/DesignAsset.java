package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** An uploaded design image. Use asset_id in a design (logo.assetId, background.image.assetId, ...). Stored re-encoded as WebP, scaled to fit 2560 px (1024 px for a logo). */
@JsonIgnoreProperties(ignoreUnknown = true)
public record DesignAsset(
        String object,
        String assetId,
        String kind,
        String url,
        long width,
        long height,
        String dominantColor,
        long bytes,
        String createdAt) {
}
