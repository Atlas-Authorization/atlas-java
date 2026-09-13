package net.atlasauth.atlas;

import java.util.List;

/** The secret key lacks permission for the operation (HTTP 403). */
public class PermissionException extends AtlasApiException {
    public PermissionException(int status, List<ErrorItem> errors) {
        super(status, errors);
    }
}
