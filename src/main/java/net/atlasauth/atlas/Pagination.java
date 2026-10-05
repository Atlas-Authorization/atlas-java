package net.atlasauth.atlas;

import net.atlasauth.atlas.models.CursorPage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Walk every page of a cursor-paginated Backend API list.
 *
 * <p>The page is the primitive most callers want, so pagination is a free helper
 * rather than bolted onto every return value — a caller who needs the whole set
 * opts into the extra round trips explicitly. Supply a fetch function that maps a
 * cursor (initially {@code null}) to the next {@link CursorPage}:
 *
 * <pre>{@code
 * for (User u : Pagination.iterate(cursor ->
 *         client.users().list(new Users.ListParams(50, cursor)))) {
 *     ...
 * }
 * List<User> all = Pagination.collect(cursor ->
 *         client.users().list(new Users.ListParams(50, cursor)));
 * }</pre>
 */
public final class Pagination {

    private Pagination() {
    }

    /** A source of pages: given the cursor to continue after, fetch the next page. */
    @FunctionalInterface
    public interface PageFetcher<T> {
        CursorPage<T> fetch(String startingAfter);
    }

    /** Lazily iterate every item across every page. */
    public static <T> Iterable<T> iterate(PageFetcher<T> fetcher) {
        return () -> new Iterator<>() {
            private String cursor = null;
            private boolean morePages = true;
            private Iterator<T> current = List.<T>of().iterator();

            private void advance() {
                while (!current.hasNext() && morePages) {
                    CursorPage<T> page = fetcher.fetch(cursor);
                    List<T> data = page.data() == null ? List.of() : page.data();
                    current = data.iterator();
                    if (page.hasMore() && page.nextCursor() != null) {
                        cursor = page.nextCursor();
                    } else {
                        morePages = false;
                    }
                }
            }

            @Override
            public boolean hasNext() {
                advance();
                return current.hasNext();
            }

            @Override
            public T next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                return current.next();
            }
        };
    }

    /**
     * Collect every page into a single list. The {@link PageFetcher} is a plain
     * {@code String -> CursorPage<T>} function, so a lambda
     * {@code cursor -> client.users().list(...)} works directly.
     */
    public static <T> List<T> collect(PageFetcher<T> fetcher) {
        List<T> out = new ArrayList<>();
        for (T item : iterate(fetcher)) {
            out.add(item);
        }
        return out;
    }
}
