package net.atlasauth.atlas;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Walks every resource reachable from {@link AtlasClient}, calls every public
 * method with placeholder arguments against a loopback backend, and checks that
 * (a) every request lands on an operation the public API documents, and (b) every
 * documented BAPI + platform operation (src/test/resources/api-operations.txt) is
 * reached by at least one method.
 */
class ApiCoverageTest {

    private MockBackend mock;
    private AtlasClient atlas;

    @BeforeEach
    void setUp() throws IOException {
        mock = MockBackend.start();
        mock.reply(200, "{}");
        atlas = AtlasClient.builder()
                .secretKey("sk_test_123")
                .platformKey("psk_test_123")
                .apiUrl(mock.baseUrl())
                .build();
    }

    @AfterEach
    void tearDown() {
        mock.close();
    }

    private record Op(String method, Pattern path, String raw) {
    }

    private static List<Op> documentedOps() throws IOException {
        List<Op> ops = new ArrayList<>();
        try (InputStream in = ApiCoverageTest.class.getResourceAsStream("/api-operations.txt")) {
            String text = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            for (String line : text.split("\n")) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                String[] parts = line.split(" ");
                String regex = "^" + parts[1].replaceAll("\\{[^}]+}", "[^/]+") + "$";
                ops.add(new Op(parts[0], Pattern.compile(regex), line));
            }
        }
        return ops;
    }

    @Test
    void everyDocumentedOperationIsReachableAndEveryCallIsDocumented() throws Exception {
        List<Op> ops = documentedOps();
        Set<String> hit = new HashSet<>();
        Set<String> undocumented = new TreeSet<>();
        Map<Object, Boolean> seen = new IdentityHashMap<>();
        List<Object> queue = new ArrayList<>();
        // the namespaces: zero-arg public accessors on AtlasClient returning a resource
        for (Method m : AtlasClient.class.getMethods()) {
            if (m.getParameterCount() == 0 && m.getReturnType().getPackageName().endsWith(".resources")) {
                queue.add(m.invoke(atlas));
            }
        }
        int calls = 0;
        while (!queue.isEmpty()) {
            Object res = queue.remove(0);
            if (res == null || seen.put(res, true) != null) {
                continue;
            }
            for (Method m : res.getClass().getMethods()) {
                if (m.getDeclaringClass() == Object.class || Modifier.isStatic(m.getModifiers())) {
                    continue;
                }
                if (m.getParameterCount() == 0 && m.getReturnType().getPackageName().endsWith(".resources")) {
                    queue.add(m.invoke(res)); // a nested sub-resource accessor
                    continue;
                }
                Object[] args = new Object[m.getParameterCount()];
                Parameter[] ps = m.getParameters();
                for (int i = 0; i < ps.length; i++) {
                    args[i] = placeholder(ps[i].getType(), ps[i].getName());
                }
                mock.method = null;
                mock.path = null;
                try {
                    m.invoke(res, args);
                } catch (InvocationTargetException e) {
                    // validation in a helper (e.g. a typed config) — the request may still be absent; coverage below decides
                }
                calls++;
                if (mock.method == null) {
                    continue;
                }
                boolean matched = false;
                for (Op op : ops) {
                    if (op.method().equals(mock.method) && op.path().matcher(mock.path).matches()) {
                        hit.add(op.raw());
                        matched = true;
                    }
                }
                if (!matched && !mock.path.equals("/v1/design/catalog")) {
                    undocumented.add(mock.method + " " + mock.path + "  <- " + res.getClass().getSimpleName() + "." + m.getName());
                }
            }
        }
        Set<String> missing = new TreeSet<>();
        for (Op op : ops) {
            if (!hit.contains(op.raw())) {
                missing.add(op.raw());
            }
        }
        assertTrue(calls > 400, "expected to exercise the whole surface, made " + calls + " calls");
        assertTrue(undocumented.isEmpty(), "calls to undocumented routes: " + undocumented);
        assertTrue(missing.isEmpty(), "documented operations no method reaches (" + missing.size() + "): " + missing);
    }

    private static Object placeholder(Class<?> type, String name) {
        if (type == String.class) {
            return name.toLowerCase().contains("contenttype") ? "image/png" : "x1";
        }
        if (type == int.class || type == Integer.class) {
            return 1;
        }
        if (type == long.class || type == Long.class) {
            return 1L;
        }
        if (type == double.class || type == Double.class) {
            return 1.0;
        }
        if (type == boolean.class || type == Boolean.class) {
            return true;
        }
        if (type == byte[].class) {
            return new byte[]{1, 2, 3};
        }
        if (type == Map.class) {
            return new LinkedHashMap<>();
        }
        if (type == List.class) {
            return new ArrayList<>();
        }
        try {
            return type.getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            return null;
        }
    }
}
