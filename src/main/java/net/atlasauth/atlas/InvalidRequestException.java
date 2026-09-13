package net.atlasauth.atlas;

import java.util.List;

/** Invalid or unprocessable request (HTTP 400 / 422). */
public class InvalidRequestException extends AtlasApiException {
    public InvalidRequestException(int status, List<ErrorItem> errors) {
        super(status, errors);
    }
}
