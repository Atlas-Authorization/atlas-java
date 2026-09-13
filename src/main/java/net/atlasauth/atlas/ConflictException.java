package net.atlasauth.atlas;

import java.util.List;

/** The request conflicts with the resource's current state (HTTP 409). */
public class ConflictException extends AtlasApiException {
    public ConflictException(int status, List<ErrorItem> errors) {
        super(status, errors);
    }
}
