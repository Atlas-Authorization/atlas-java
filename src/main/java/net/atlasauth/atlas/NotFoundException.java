package net.atlasauth.atlas;

import java.util.List;

/** The requested resource does not exist (HTTP 404). */
public class NotFoundException extends AtlasApiException {
    public NotFoundException(int status, List<ErrorItem> errors) {
        super(status, errors);
    }
}
