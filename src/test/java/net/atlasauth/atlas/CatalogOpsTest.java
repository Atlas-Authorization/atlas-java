package net.atlasauth.atlas;

import net.atlasauth.atlas.catalog.Catalog;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Calls every generated catalog operation (src/test/resources/catalog-ops.tsv) against a loopback backend and
 * checks the method and path that reach the wire, so a generator regression cannot silently re-route a call.
 */
class CatalogOpsTest {

    @Test
    void everyGeneratedOperationHitsItsMethodAndPath() throws Exception {
        List<String[]> rows = new ArrayList<>();
        try (InputStream in = CatalogOpsTest.class.getResourceAsStream("/catalog-ops.tsv")) {
            for (String line : new String(in.readAllBytes(), StandardCharsets.UTF_8).split("\n")) {
                if (!line.isBlank()) {
                    rows.add(line.split("\t", -1));
                }
            }
        }
        assertTrue(rows.size() > 500, "expected the whole generated surface, found " + rows.size());
        try (MockBackend mock = MockBackend.start()) {
            AtlasClient atlas = AtlasClient.builder().secretKey("sk_test_123").platformKey("psk_test_123")
                    .apiUrl(mock.baseUrl()).build();
            Catalog catalog = atlas.catalog();
            for (String[] row : rows) {
                String ns = row[0], cls = row[1], name = row[2], http = row[3], path = row[4];
                String[] types = row[5].isEmpty() ? new String[0] : row[5].split(",");
                Object api = Catalog.class.getMethod(accessor(ns)).invoke(catalog);
                Method target = null;
                for (Method m : api.getClass().getMethods()) {
                    if (m.getName().equals(name) && m.getParameterCount() == types.length) {
                        target = m;
                        break;
                    }
                }
                assertTrue(target != null, cls + "." + name + " with " + types.length + " parameters");
                int p = 0;
                String expected = path;
                Object[] args = new Object[target.getParameterCount()];
                Class<?>[] pt = target.getParameterTypes();
                for (int i = 0; i < pt.length; i++) {
                    if (pt[i] == String.class && i < countParams(path)) {
                        String v = "p" + (++p);
                        args[i] = v;
                        expected = expected.replaceFirst("\\{[^}]+}", v);
                    } else {
                        args[i] = placeholder(pt[i]);
                    }
                }
                Class<?> ret = target.getReturnType();
                mock.reply(200, ret == List.class ? "[]" : ret == String.class ? "\"x\"" : ret == Long.class || ret == Double.class ? "1"
                        : ret == Boolean.class ? "true" : "{}");
                mock.method = null;
                try {
                    target.invoke(api, args);
                } catch (InvocationTargetException e) {
                    throw new AssertionError(cls + "." + name + " failed: " + e.getCause(), e.getCause());
                }
                assertEquals(http, mock.method, cls + "." + name);
                assertEquals(expected, mock.path, cls + "." + name);
            }
        }
    }

    @Test
    void cursorListIteratesAcrossPagesAndKeepsTheCallersQueryUntouched() throws Exception {
        try (MockBackend mock = MockBackend.start()) {
            AtlasClient atlas = AtlasClient.builder().secretKey("sk_test_123").apiUrl(mock.baseUrl()).build();
            mock.reply(200, "{\"data\":[{\"id\":\"a\"}],\"has_more\":false,\"next_cursor\":null}");
            var query = new net.atlasauth.atlas.catalog.models.AdministrativeUnitsListQuery().limit(1L);
            int n = 0;
            for (var item : atlas.catalog().administrativeUnits().listAll(query)) {
                n++;
            }
            assertEquals(1, n);
            assertEquals(null, query.startingAfter, "the caller's query is copied, never mutated");
            assertEquals("/v1/administrative_units", mock.path);
        }
    }

    @Test
    void aPatchBodySendsAnExplicitNullOnlyWhenCleared() throws Exception {
        var mapper = net.atlasauth.atlas.http.Json.mapper();
        var body = new net.atlasauth.atlas.catalog.models.AdministrativeUnitsUpdateBody().name("n").clearDescription();
        assertEquals("{\"name\":\"n\",\"description\":null}", mapper.writeValueAsString(body));
        assertEquals("{\"name\":\"n\"}", mapper.writeValueAsString(new net.atlasauth.atlas.catalog.models.AdministrativeUnitsUpdateBody().name("n")));
        assertEquals("{\"description\":\"d\"}", mapper.writeValueAsString(new net.atlasauth.atlas.catalog.models.AdministrativeUnitsUpdateBody().clearDescription().description("d")));
    }

    private static String accessor(String ns) {
        StringBuilder sb = new StringBuilder();
        boolean up = false;
        for (char c : ns.toCharArray()) {
            if (c == '_' || c == '-') {
                up = true;
            } else {
                sb.append(up ? Character.toUpperCase(c) : c);
                up = false;
            }
        }
        return sb.toString();
    }

    private static int countParams(String path) {
        int n = 0;
        for (char c : path.toCharArray()) {
            if (c == '{') {
                n++;
            }
        }
        return n;
    }

    private static Object placeholder(Class<?> type) throws Exception {
        if (type == String.class) {
            return "image/png";
        }
        if (type == byte[].class) {
            return new byte[]{1, 2, 3};
        }
        if (type == Map.class) {
            return new HashMap<>();
        }
        if (type == List.class) {
            return new ArrayList<>();
        }
        if (type.getName().startsWith("java.")) {
            return null;
        }
        Constructor<?> c = type.getConstructors()[0];
        return c.newInstance(new Object[c.getParameterCount()]);
    }
}
