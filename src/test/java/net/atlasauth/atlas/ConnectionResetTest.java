package net.atlasauth.atlas;

import net.atlasauth.atlas.models.CreateUserBody;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/** A connection closed before any response is retried once, for safe requests only. */
class ConnectionResetTest {

    /** Closes the first {@code drops} connections without a byte, then answers {@code {"id":"usr_1"}}. */
    private static final class Flaky implements AutoCloseable {
        final ServerSocket server;
        final AtomicInteger connections = new AtomicInteger();

        Flaky(int drops) throws IOException {
            server = new ServerSocket(0, 50, java.net.InetAddress.getLoopbackAddress());
            Thread t = new Thread(() -> {
                try {
                    while (true) {
                        Socket s = server.accept();
                        if (connections.incrementAndGet() <= drops) {
                            s.close();
                            continue;
                        }
                        byte[] buf = new byte[8192];
                        s.getInputStream().read(buf);
                        byte[] body = "{\"id\":\"usr_1\"}".getBytes(StandardCharsets.UTF_8);
                        OutputStream out = s.getOutputStream();
                        out.write(("HTTP/1.1 200 OK\r\nContent-Type: application/json\r\nContent-Length: " + body.length
                                + "\r\nConnection: close\r\n\r\n").getBytes(StandardCharsets.UTF_8));
                        out.write(body);
                        out.flush();
                        s.close();
                    }
                } catch (IOException ignored) {
                    // closed by the test
                }
            });
            t.setDaemon(true);
            t.start();
        }

        AtlasClient client() {
            return AtlasClient.builder().secretKey("sk_test_123").apiUrl("http://127.0.0.1:" + server.getLocalPort()).build();
        }

        @Override
        public void close() throws IOException {
            server.close();
        }
    }

    @Test
    void getSurvivesOneReset() throws Exception {
        try (Flaky f = new Flaky(1)) {
            assertEquals("usr_1", f.client().users().get("usr_1").id());
        }
    }

    @Test
    void keyedWriteSurvivesOneReset() throws Exception {
        try (Flaky f = new Flaky(1)) {
            f.client().users().create(new CreateUserBody(), "idem-1");
            assertEquals(2, f.connections.get());
        }
    }

    @Test
    void plainWriteIsNotRetried() throws Exception {
        try (Flaky f = new Flaky(1)) {
            assertThrows(AtlasConnectionException.class, () -> f.client().users().create(new CreateUserBody()));
            assertEquals(1, f.connections.get());
        }
    }

    @Test
    void secondResetIsRaised() throws Exception {
        try (Flaky f = new Flaky(2)) {
            assertThrows(AtlasConnectionException.class, () -> f.client().users().get("usr_1"));
            assertEquals(2, f.connections.get()); // one try and exactly one retry, not one per layer
        }
    }

    @Test
    void keyedWriteSecondResetIsRaised() throws Exception {
        try (Flaky f = new Flaky(2)) {
            assertThrows(AtlasConnectionException.class, () -> f.client().users().create(new CreateUserBody(), "idem-2"));
            assertEquals(2, f.connections.get());
        }
    }
}
