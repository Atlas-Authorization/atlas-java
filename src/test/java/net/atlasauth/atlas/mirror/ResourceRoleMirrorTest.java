package net.atlasauth.atlas.mirror;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ResourceRoleMirrorTest {

    @Test
    void inheritsDownTheTreeAndHonoursIncludesAndInheritFlag() {
        var store = new ResourceRoleMirror.MemoryStore();
        store.replaceTypes(List.of(new ResourceRoleMirror.ResourceType("doc", true, List.of(
                new ResourceRoleMirror.Role("viewer", List.of("read"), List.of()),
                new ResourceRoleMirror.Role("editor", List.of("write"), List.of("viewer"))))));
        store.replaceResources(List.of(new ResourceRoleMirror.Resource("r_parent", "doc", "p", null),
                new ResourceRoleMirror.Resource("r_child", "doc", "c", "r_parent")));
        store.applyChange(true, new ResourceRoleMirror.Assignment("a1", "r_parent", "user:u1", "editor", true));
        store.applyChange(true, new ResourceRoleMirror.Assignment("a2", "r_parent", "user:u2", "editor", false));
        ResourceRoleMirror m = ResourceRoleMirror.builder(new Backend() {
            public com.fasterxml.jackson.databind.JsonNode createApiKey(java.util.Map<String, Object> b, String k) { return null; }
            public void deleteApiKey(String id) { }
            public com.fasterxml.jackson.databind.JsonNode listRevokedApiKeys(String s, int l) { return null; }
        }, store).build();
        var child = ResourceRoleMirror.ResourceRef.byExternalId("doc", "c");
        assertTrue(m.can("u1", "read", child, null, null), "editor includes viewer, inherited by the child");
        assertFalse(m.can("u2", "read", child, null, null), "an assignment with inherit=false stays on its resource");
        assertTrue(m.can("u3", "write", child, List.of(), List.of()) == false);
        store.applyChange(true, new ResourceRoleMirror.Assignment("a3", "r_parent", "org:o1", "viewer", true));
        assertTrue(m.can("u3", "read", child, List.of("o1"), null));
        store.applyChange(false, new ResourceRoleMirror.Assignment("a3", "r_parent", "org:o1", "viewer", true));
        assertFalse(m.can("u3", "read", child, List.of("o1"), null));
        assertEquals("unknown_resource", m.check("user", "u1", "read", ResourceRoleMirror.ResourceRef.byId("nope"), null, null).code());
    }
}
