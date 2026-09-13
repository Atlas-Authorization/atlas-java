package net.atlasauth.atlas;

import java.util.List;

/** Missing or invalid secret key (HTTP 401). */
public class AuthenticationException extends AtlasApiException {
    public AuthenticationException(int status, List<ErrorItem> errors) {
        super(status, errors);
    }
}
