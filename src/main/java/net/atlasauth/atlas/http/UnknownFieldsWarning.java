package net.atlasauth.atlas.http;

import java.util.List;

/**
 * Reported when a write succeeded but the server ignored some request-body
 * fields it does not accept (the {@code Atlas-Unknown-Fields} response header).
 * Nested fields are named {@code parent.child}.
 *
 * <p>Usually a typo or a field your SDK version sends that the endpoint does not
 * take. Turn on strict bodies ({@code AtlasClient.builder().strict(true)}) to get a
 * {@code 422 UNKNOWN_FIELD} error instead, before anything is changed.
 *
 * @param method the HTTP method of the request
 * @param path   the request path (no query string)
 * @param fields the ignored field names, in the order the server listed them
 */
public record UnknownFieldsWarning(String method, String path, List<String> fields) {
}
