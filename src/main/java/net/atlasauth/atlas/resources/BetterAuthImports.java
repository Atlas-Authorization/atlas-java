package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.BetterAuthImport;
import net.atlasauth.atlas.models.BetterAuthImportKey;
import net.atlasauth.atlas.models.BetterAuthImportOptions;
import net.atlasauth.atlas.models.BetterAuthImportPart;
import net.atlasauth.atlas.models.CursorPage;
import net.atlasauth.atlas.models.DataList;
import net.atlasauth.atlas.models.DeletedBetterAuthImport;
import net.atlasauth.atlas.models.DeletedImportedJwk;
import net.atlasauth.atlas.models.ImportedJwk;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static net.atlasauth.atlas.http.Transport.enc;

/**
 * Migrate from Better Auth ({@code /v1/better_auth_imports}): import a sealed bundle made by the Better
 * Auth exporter ({@code npx atlas-better-auth-export}).
 *
 * <ol>
 *   <li>{@link #getPublicKey()}: the key the exporter seals the bundle to.</li>
 *   <li>{@link #upload(String)} (or {@link #create()}, {@link #uploadPart} per part and {@link #finalize(String)}):
 *       Atlas opens the bundle and runs the dry run.</li>
 *   <li>Read the import's {@code report}, then {@link #apply(String)}. Applying again after a failure resumes
 *       where it stopped; every record is an idempotent upsert.</li>
 * </ol>
 */
public final class BetterAuthImports {

    /** Bytes per uploaded bundle part (the API takes at most 10 MiB per part). */
    public static final int PART_BYTES = 8 * 1024 * 1024;

    /** How {@link #waitFor} waits between polls. */
    @FunctionalInterface
    public interface Sleeper {
        void sleep(Duration d) throws InterruptedException;
    }

    private final Transport t;
    private final Sleeper sleeper;

    public BetterAuthImports(Transport t) {
        this(t, d -> Thread.sleep(d.toMillis()));
    }

    /** With a custom {@link Sleeper} (tests). */
    public BetterAuthImports(Transport t, Sleeper sleeper) {
        this.t = t;
        this.sleeper = sleeper;
    }

    /** {@code GET /v1/better_auth_imports/public_key} — the instance's import key (created on first read). */
    public BetterAuthImportKey getPublicKey() {
        return t.get("/v1/better_auth_imports/public_key", new TypeReference<>() {
        });
    }

    /** {@code POST /v1/better_auth_imports/public_key/rotate} — a new key; bundles sealed to the old one no longer finalize. */
    public BetterAuthImportKey rotatePublicKey() {
        return rotatePublicKey(null);
    }

    public BetterAuthImportKey rotatePublicKey(String idempotencyKey) {
        return t.request("POST", "/v1/better_auth_imports/public_key/rotate", null, null, idempotencyKey, false,
                new TypeReference<>() {
                });
    }

    /** {@code POST /v1/better_auth_imports} — start an import (status {@code uploading}) with the default options. */
    public BetterAuthImport create() {
        return create(null, null);
    }

    /** {@code POST /v1/better_auth_imports} — start an import with {@code options}. */
    public BetterAuthImport create(BetterAuthImportOptions options) {
        return create(options, null);
    }

    public BetterAuthImport create(BetterAuthImportOptions options, String idempotencyKey) {
        return t.post("/v1/better_auth_imports", optionsBody(options), idempotencyKey, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/better_auth_imports} — newest first, cursor-paginated. */
    public CursorPage<BetterAuthImport> list() {
        return list(null, null);
    }

    public CursorPage<BetterAuthImport> list(Integer limit, String startingAfter) {
        Map<String, Object> q = Params.of().set("limit", limit).set("starting_after", startingAfter).map();
        return t.get("/v1/better_auth_imports", q, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/better_auth_imports/{id}} — status, progress, the dry-run {@code report} and the {@code result}. */
    public BetterAuthImport get(String importId) {
        return t.get("/v1/better_auth_imports/" + enc(importId), new TypeReference<>() {
        });
    }

    /**
     * {@code PUT /v1/better_auth_imports/{id}/parts/{part}} — upload part {@code part} (1-based) of the sealed
     * bundle as {@code text/plain}: whole lines, at most 10 MiB. Re-sending a part replaces it.
     */
    public BetterAuthImportPart uploadPart(String importId, int part, String data) {
        return t.requestBytes("PUT", "/v1/better_auth_imports/" + enc(importId) + "/parts/" + part, null,
                data.getBytes(StandardCharsets.UTF_8), "text/plain; charset=utf-8", new TypeReference<>() {
                });
    }

    /**
     * {@code POST /v1/better_auth_imports/{id}/finalize} — close the upload; Atlas checks the parts and the key,
     * then runs the dry run (inline up to 4 MiB: {@code ready} / {@code invalid}; above that {@code analyzing},
     * see {@link #waitFor(String)}).
     */
    public BetterAuthImport finalize(String importId) {
        return t.post("/v1/better_auth_imports/" + enc(importId) + "/finalize", null, new TypeReference<>() {
        });
    }

    /** {@code POST /v1/better_auth_imports/{id}/dry_run} — run the dry run again with the stored options. */
    public BetterAuthImport dryRun(String importId) {
        return dryRun(importId, null);
    }

    /** {@code POST /v1/better_auth_imports/{id}/dry_run} — run the dry run again; new options replace the stored ones. */
    public BetterAuthImport dryRun(String importId, BetterAuthImportOptions options) {
        return t.post("/v1/better_auth_imports/" + enc(importId) + "/dry_run", optionsBody(options),
                new TypeReference<>() {
                });
    }

    /** {@code POST /v1/better_auth_imports/{id}/apply} — import, or resume a {@code failed} import from its cursor. */
    public BetterAuthImport apply(String importId) {
        return apply(importId, false);
    }

    /** As {@link #apply(String)}; {@code confirmConflicts} accepts the conflicts the dry run reported (else 409). */
    public BetterAuthImport apply(String importId, boolean confirmConflicts) {
        Map<String, Object> body = new LinkedHashMap<>();
        if (confirmConflicts) {
            body.put("confirm_conflicts", true);
        }
        return t.post("/v1/better_auth_imports/" + enc(importId) + "/apply", body, new TypeReference<>() {
        });
    }

    /** {@code DELETE /v1/better_auth_imports/{id}} — remove the import and its stored bundle; imported data stays. */
    public DeletedBetterAuthImport delete(String importId) {
        return t.delete("/v1/better_auth_imports/" + enc(importId), new TypeReference<>() {
        });
    }

    /** {@code GET /v1/better_auth_imports/jwks} — the Better Auth JWT keys published in the instance JWKS. */
    public DataList<ImportedJwk> listJwks() {
        return t.get("/v1/better_auth_imports/jwks", new TypeReference<>() {
        });
    }

    /** {@code DELETE /v1/better_auth_imports/jwks/{kid}} — stop publishing one imported key. */
    public DeletedImportedJwk deleteJwk(String kid) {
        return t.delete("/v1/better_auth_imports/jwks/" + enc(kid), new TypeReference<>() {
        });
    }

    // ---- helpers ------------------------------------------------------------------------------

    /** {@link #upload(String, BetterAuthImportOptions, int, BiConsumer)} with the default options and part size. */
    public BetterAuthImport upload(String bundle) {
        return upload(bundle, null, PART_BYTES, null);
    }

    /**
     * Create an import, upload the sealed bundle in line-aligned parts of at most {@code partBytes} and
     * finalize it. Returns the finalized import ({@code ready} / {@code invalid}, or {@code analyzing} for a
     * large bundle; then {@link #waitFor(String)}). {@code onPart} (nullable) gets (part, total).
     */
    public BetterAuthImport upload(String bundle, BetterAuthImportOptions options, int partBytes,
                                   BiConsumer<Integer, Integer> onPart) {
        List<String> parts = splitBundleParts(bundle, partBytes);
        if (parts.isEmpty()) {
            throw new IllegalArgumentException("The bundle is empty.");
        }
        BetterAuthImport created = create(options);
        String id = created == null ? null : created.id();
        if (id == null || id.isEmpty()) {
            throw new IllegalStateException("Atlas did not return an import id.");
        }
        for (int i = 0; i < parts.size(); i++) {
            uploadPart(id, i + 1, parts.get(i));
            if (onPart != null) {
                onPart.accept(i + 1, parts.size());
            }
        }
        return finalize(id);
    }

    /** {@link #waitFor(String, Duration, Duration, Consumer)} polling every 2 s for up to 6 hours. */
    public BetterAuthImport waitFor(String importId) {
        return waitFor(importId, Duration.ofSeconds(2), Duration.ofHours(6), null);
    }

    /**
     * Poll until the import leaves {@code analyzing} / {@code queued} / {@code applying} and return it
     * ({@code ready}, {@code invalid}, {@code completed} or {@code failed}). {@code onProgress} (nullable) sees
     * every poll. Throws {@link IllegalStateException} after {@code timeout}.
     */
    public BetterAuthImport waitFor(String importId, Duration interval, Duration timeout,
                                    Consumer<BetterAuthImport> onProgress) {
        long deadline = System.nanoTime() + timeout.toNanos();
        Duration waited = Duration.ZERO;
        while (true) {
            BetterAuthImport imp = get(importId);
            if (onProgress != null) {
                onProgress.accept(imp);
            }
            if (imp == null || !imp.inFlight()) {
                return imp;
            }
            if (System.nanoTime() > deadline || waited.compareTo(timeout) >= 0) {
                throw new IllegalStateException("Timed out waiting for Better Auth import " + importId
                        + " (status " + imp.status() + ").");
            }
            try {
                sleeper.sleep(interval);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("Interrupted while waiting for Better Auth import " + importId, e);
            }
            waited = waited.plus(interval);
        }
    }

    /**
     * Split a sealed bundle into upload parts of at most {@code maxBytes} (UTF-8), each ending on a line
     * boundary. Empty lines are dropped; a single line longer than {@code maxBytes} becomes a part of its own
     * (the API refuses parts over 10 MiB, so keep {@code maxBytes} at or under {@link #PART_BYTES}).
     */
    public static List<String> splitBundleParts(String bundle, int maxBytes) {
        if (maxBytes <= 0) {
            throw new IllegalArgumentException("maxBytes must be positive.");
        }
        List<String> parts = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        long size = 0;
        for (String line : bundle.split("\n", -1)) {
            if (line.endsWith("\r")) {
                line = line.substring(0, line.length() - 1);
            }
            if (line.isEmpty()) {
                continue;
            }
            int n = line.getBytes(StandardCharsets.UTF_8).length + 1;
            if (size > 0 && size + n > maxBytes) {
                parts.add(current.toString());
                current.setLength(0);
                size = 0;
            }
            current.append(line).append('\n');
            size += n;
        }
        if (size > 0) {
            parts.add(current.toString());
        }
        return parts;
    }

    private static Map<String, Object> optionsBody(BetterAuthImportOptions options) {
        Map<String, Object> body = new LinkedHashMap<>();
        if (options != null) {
            body.put("options", options);
        }
        return body;
    }
}
