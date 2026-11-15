package net.atlasauth.atlas.machine;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.atlasauth.atlas.http.Json;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.PosixFilePermissions;

/** Where a machine's identity lives; implement it to use a keychain or secret manager. */
public interface MachineStore {

    /** The stored identity, or null when there is none. */
    MachineIdentity load();

    void save(MachineIdentity identity);

    void clear();

    /** A store that forgets on exit (tests, short-lived jobs that enrol every run). */
    static MachineStore memory() {
        return new MachineStore() {
            private volatile MachineIdentity value;

            @Override
            public MachineIdentity load() {
                return value;
            }

            @Override
            public void save(MachineIdentity identity) {
                value = identity;
            }

            @Override
            public void clear() {
                value = null;
            }
        };
    }

    /** A store backed by one file, written 0600 in a 0700 directory (where the platform has POSIX permissions). */
    static MachineStore file(Path path) {
        ObjectMapper mapper = Json.mapper();
        return new MachineStore() {
            @Override
            public MachineIdentity load() {
                try {
                    return mapper.readValue(Files.readAllBytes(path), MachineIdentity.class);
                } catch (IOException e) {
                    return null;
                }
            }

            @Override
            public void save(MachineIdentity identity) {
                try {
                    Path dir = path.toAbsolutePath().getParent();
                    Files.createDirectories(dir);
                    boolean posix = dir.getFileSystem().supportedFileAttributeViews().contains("posix");
                    if (posix) {
                        Files.setPosixFilePermissions(dir, PosixFilePermissions.fromString("rwx------"));
                    }
                    Path tmp = Files.createTempFile(dir, path.getFileName().toString(), ".tmp");
                    if (posix) {
                        Files.setPosixFilePermissions(tmp, PosixFilePermissions.fromString("rw-------"));
                    }
                    Files.write(tmp, mapper.writeValueAsBytes(identity));
                    Files.move(tmp, path, StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }

            @Override
            public void clear() {
                try {
                    Files.deleteIfExists(path);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }
        };
    }
}
