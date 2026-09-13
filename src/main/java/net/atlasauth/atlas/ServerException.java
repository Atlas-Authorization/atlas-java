package net.atlasauth.atlas;

import java.util.List;

/** The Atlas API encountered an internal error (HTTP 5xx). */
public class ServerException extends AtlasApiException {
    public ServerException(int status, List<ErrorItem> errors) {
        super(status, errors);
    }
}
