package net.atlasauth.atlas;

import java.util.List;

/** Rate limit exceeded (HTTP 429); inspect the error meta for retry_after. */
public class RateLimitException extends AtlasApiException {
    public RateLimitException(int status, List<ErrorItem> errors) {
        super(status, errors);
    }
}
