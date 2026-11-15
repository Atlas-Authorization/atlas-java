package net.atlasauth.atlas.mirror;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.LongSupplier;

/**
 * A local mirror of resource-scoped role assignments, so {@code can(user, permission, resource)} is answered
 * with no network call and the SAME inheritance rules as {@code POST /v1/resource_checks}.
 *
 * <p>It keeps the resource types (roles, {@code includes}, {@code inherit}), the resource tree (type, external
 * id, parent) and the role assignments. {@link #sync()} pulls the types and the tree, then consumes
 * {@code GET /v1/resource_role_assignments/changes} from the stored cursor; {@link #applyWebhook} applies the
 * {@code resource_role_assignment.created} / {@code .deleted} events between syncs.
 *
 * <p>One difference from the server: Atlas adds the user's organizations and groups to the subject itself.
 * Offline you pass them: {@code can(user, perm, res, orgs, groups)}.
 */
public final class ResourceRoleMirror {

    /** A role of a resource type. */
    public record Role(String key, List<String> permissions, List<String> includes) {
    }

    public record ResourceType(String key, boolean inherit, List<Role> roles) {
    }

    public record Resource(String id, String type, String externalId, String parentId) {
    }

    /** {@code subject} is {@code user:usr_1}, {@code group:eng} or {@code org:org_1}. */
    public record Assignment(String id, String resourceId, String subject, String role, boolean inherit) {
    }

    /** A reference to a resource: Atlas {@code id}, or your own {@code type} + {@code externalId}. */
    public record ResourceRef(String id, String type, String externalId) {
        public static ResourceRef byId(String id) {
            return new ResourceRef(id, null, null);
        }

        public static ResourceRef byExternalId(String type, String externalId) {
            return new ResourceRef(null, type, externalId);
        }
    }

    public record Grant(String resourceId, String role, String subject, boolean inherited) {
    }

    /** The verdict and the grants behind it. {@code code}: {@code unknown_resource} or {@code stale}, else null. */
    public record Check(boolean allowed, String resourceId, List<Grant> grants, String code, boolean stale) {
    }

    /** Where the mirror keeps its data. {@link MemoryStore} is for tests and single-process apps. */
    public interface Store {
        String getCursor();

        void setCursor(String cursor);

        ResourceType getType(String key);

        Resource getResource(ResourceRef ref);

        /** Every assignment sitting on any of these resources. */
        List<Assignment> assignmentsOn(List<String> resourceIds);

        void replaceTypes(List<ResourceType> types);

        void replaceResources(List<Resource> resources);

        /** Upsert by assignment id ({@code created}) or remove it ({@code deleted}). */
        void applyChange(boolean created, Assignment assignment);

        default Long getLastSyncAt() {
            return null;
        }

        default void setLastSyncAt(long at) {
        }
    }

    /** An in-memory {@link Store}. */
    public static final class MemoryStore implements Store {
        private final Map<String, Assignment> assignments = new ConcurrentHashMap<>();
        private volatile Map<String, Resource> resources = Map.of();
        private volatile Map<String, ResourceType> types = Map.of();
        private volatile String cursor;
        private volatile Long lastSyncAt;

        @Override
        public String getCursor() {
            return cursor;
        }

        @Override
        public void setCursor(String cursor) {
            this.cursor = cursor;
        }

        @Override
        public ResourceType getType(String key) {
            return types.get(key);
        }

        @Override
        public Resource getResource(ResourceRef ref) {
            if (ref.id() != null) {
                return resources.get(ref.id());
            }
            return resources.values().stream().filter(r -> r.type().equals(ref.type()) && java.util.Objects.equals(r.externalId(), ref.externalId())).findFirst().orElse(null);
        }

        @Override
        public List<Assignment> assignmentsOn(List<String> ids) {
            Set<String> want = new HashSet<>(ids);
            return assignments.values().stream().filter(a -> want.contains(a.resourceId())).toList();
        }

        @Override
        public void replaceTypes(List<ResourceType> next) {
            Map<String, ResourceType> m = new HashMap<>();
            next.forEach(t -> m.put(t.key(), t));
            types = m;
        }

        @Override
        public void replaceResources(List<Resource> next) {
            Map<String, Resource> m = new HashMap<>();
            next.forEach(r -> m.put(r.id(), r));
            resources = m;
        }

        @Override
        public void applyChange(boolean created, Assignment a) {
            if (created) {
                assignments.put(a.id(), a);
            } else {
                assignments.remove(a.id());
            }
        }

        @Override
        public Long getLastSyncAt() {
            return lastSyncAt;
        }

        @Override
        public void setLastSyncAt(long at) {
            lastSyncAt = at;
        }
    }

    private static final int MAX_DEPTH = 16;

    private final Backend backend;
    private final Store store;
    private final Long maxStalenessMs;
    private final long serveStaleForMs;
    private final KeyMirror.StaleListener onServeStale;
    private final long treeRefreshMs;
    private final LongSupplier now;
    private volatile Long memorySyncAt;
    private volatile Long treeAt;

    private ResourceRoleMirror(Builder b) {
        this.backend = b.backend;
        this.store = b.store;
        this.maxStalenessMs = b.maxStalenessMs;
        this.serveStaleForMs = b.serveStaleForMs;
        this.onServeStale = b.onServeStale;
        this.treeRefreshMs = b.treeRefreshMs;
        this.now = b.now;
    }

    public static Builder builder(Backend backend, Store store) {
        return new Builder(backend, store);
    }

    /** All permissions a role grants, following {@code includes} (cycle-safe). {@code *} means every permission. */
    static Set<String> rolePermissions(ResourceType type, String roleKey, Set<String> seen) {
        Set<String> out = new LinkedHashSet<>();
        if (!seen.add(roleKey)) {
            return out;
        }
        Role role = type.roles().stream().filter(r -> r.key().equals(roleKey)).findFirst().orElse(null);
        if (role == null) {
            return out;
        }
        out.addAll(role.permissions());
        for (String inc : role.includes() == null ? List.<String>of() : role.includes()) {
            out.addAll(rolePermissions(type, inc, seen));
        }
        return out;
    }

    /** One link of the resource chain, resource first, then its ancestors. */
    public record Link(String resourceId, ResourceType type, List<Assignment> assignments) {
    }

    /** The server's {@code resolveResourceGrants}: the grants a subject holds for a permission over a chain. */
    public static List<Grant> resolveGrants(List<Link> chain, Set<String> subjects, String permission) {
        List<Grant> grants = new ArrayList<>();
        for (int i = 0; i < chain.size(); i++) {
            Link link = chain.get(i);
            for (Assignment a : link.assignments()) {
                if (!subjects.contains(a.subject()) || (i > 0 && !a.inherit())) {
                    continue;
                }
                Set<String> perms = rolePermissions(link.type(), a.role(), new HashSet<>());
                if (perms.contains(permission) || perms.contains("*")) {
                    grants.add(new Grant(link.resourceId(), a.role(), a.subject(), i > 0));
                }
            }
            if (!link.type().inherit()) {
                break;
            }
        }
        return grants;
    }

    private List<Link> chainOf(Resource resource) {
        List<Resource> rows = new ArrayList<>(List.of(resource));
        String id = resource.parentId();
        for (int i = 0; i < MAX_DEPTH && id != null; i++) {
            Resource row = store.getResource(ResourceRef.byId(id));
            String seenId = id;
            if (row == null || rows.stream().anyMatch(r -> r.id().equals(seenId))) {
                break;
            }
            rows.add(row);
            id = row.parentId();
        }
        List<Assignment> assignments = store.assignmentsOn(rows.stream().map(Resource::id).toList());
        List<Link> chain = new ArrayList<>();
        for (Resource r : rows) {
            ResourceType t = store.getType(r.type());
            chain.add(new Link(r.id(), t != null ? t : new ResourceType(r.type(), true, List.of()),
                    assignments.stream().filter(a -> a.resourceId().equals(r.id())).toList()));
        }
        return chain;
    }

    /** The verdict and the grants behind it, in the shape of {@code POST /v1/resource_checks}. */
    public Check check(String subjectType, String subjectId, String permission, ResourceRef resource, List<String> orgs, List<String> groups) {
        boolean servedStale = false;
        if (maxStalenessMs != null) {
            Long at = lastSyncAt();
            double behind = at == null ? Double.POSITIVE_INFINITY : now.getAsLong() - at - maxStalenessMs;
            if (behind > 0) {
                if (at == null || behind > serveStaleForMs) {
                    return new Check(false, null, List.of(), "stale", false);
                }
                servedStale = true;
                if (onServeStale != null) {
                    onServeStale.served(at, (long) behind);
                }
            }
        }
        Resource row = store.getResource(resource);
        if (row == null || permission == null || permission.isEmpty()) {
            return new Check(false, row == null ? null : row.id(), List.of(), row == null ? "unknown_resource" : null, false);
        }
        Set<String> subjects = new LinkedHashSet<>();
        subjects.add(subjectType + ":" + subjectId);
        for (String g : groups == null ? List.<String>of() : groups) {
            subjects.add("group:" + g);
        }
        for (String o : orgs == null ? List.<String>of() : orgs) {
            subjects.add("org:" + o);
        }
        List<Grant> grants = resolveGrants(chainOf(row), subjects, permission);
        return new Check(!grants.isEmpty(), row.id(), grants, null, servedStale);
    }

    /** May the user hold {@code permission} on {@code resource}? Answered locally. */
    public boolean can(String userId, String permission, ResourceRef resource, List<String> orgs, List<String> groups) {
        return check("user", userId, permission, resource, orgs, groups).allowed();
    }

    /** Epoch ms of the last completed {@link #sync()}; null = never. */
    public Long lastSyncAt() {
        Long stored = store.getLastSyncAt();
        return stored != null ? stored : memorySyncAt;
    }

    /**
     * Pull the types and the resource tree (when {@code treeRefreshMs} has passed), then consume the assignment
     * change feed from the stored cursor. Call it on a timer and at startup.
     *
     * @return how many changes it applied
     */
    public int sync() {
        Long ta = treeAt;
        if (ta == null || now.getAsLong() - ta >= treeRefreshMs) {
            List<ResourceType> types = new ArrayList<>();
            // resource types are a short registry: one page
            JsonNode typePage = backend.listResourceTypes();
            for (JsonNode t : typePage.path("data")) {
                List<Role> roles = new ArrayList<>();
                for (JsonNode r : t.path("roles")) {
                    roles.add(new Role(r.path("key").asText(), strings(r.path("permissions")), strings(r.path("includes"))));
                }
                types.add(new ResourceType(t.path("key").asText(), t.path("inherit").asBoolean(true), roles));
            }
            List<Resource> tree = new ArrayList<>();
            String cursor = null;
            while (true) {
                JsonNode page = backend.listResources(cursor, 1000);
                for (JsonNode r : page.path("data")) {
                    tree.add(new Resource(r.path("id").asText(), r.path("type").asText(), r.path("external_id").asText(null),
                            r.hasNonNull("parent_id") ? r.get("parent_id").asText() : null));
                }
                if (!page.path("has_more").asBoolean(false) || !page.hasNonNull("next_cursor")) {
                    break;
                }
                cursor = page.get("next_cursor").asText();
            }
            store.replaceTypes(types);
            store.replaceResources(tree);
            treeAt = now.getAsLong();
        }
        String after = store.getCursor();
        int applied = 0;
        while (true) {
            JsonNode page = backend.listResourceRoleAssignmentChanges(after, 500);
            for (JsonNode c : page.path("data")) {
                store.applyChange("created".equals(c.path("change").asText()), new Assignment(c.path("assignment_id").asText(),
                        c.path("resource_id").asText(), c.path("subject").path("type").asText() + ":" + c.path("subject").path("id").asText(),
                        c.path("role").asText(), c.path("inherit").asBoolean(true)));
                applied++;
            }
            if (page.hasNonNull("next_cursor")) {
                after = page.get("next_cursor").asText();
                store.setCursor(after);
            }
            if (!page.path("has_more").asBoolean(false)) {
                memorySyncAt = now.getAsLong();
                store.setLastSyncAt(memorySyncAt);
                return applied;
            }
        }
    }

    /**
     * Apply a VERIFIED {@code resource_role_assignment.created} / {@code .deleted} webhook (check the
     * signature first).
     *
     * @return whether the event changed the mirror
     */
    public boolean applyWebhook(String type, JsonNode d) {
        if (!"resource_role_assignment.created".equals(type) && !"resource_role_assignment.deleted".equals(type)) {
            return false;
        }
        if (d == null || !d.path("id").isTextual() || !d.path("resource_id").isTextual() || !d.path("role").isTextual()
                || !d.path("subject").path("type").isTextual() || !d.path("subject").path("id").isTextual()) {
            return false;
        }
        store.applyChange("resource_role_assignment.created".equals(type), new Assignment(d.get("id").asText(), d.get("resource_id").asText(),
                d.get("subject").get("type").asText() + ":" + d.get("subject").get("id").asText(), d.get("role").asText(),
                !d.has("inherit") || d.get("inherit").asBoolean(true)));
        return true;
    }

    private static List<String> strings(JsonNode arr) {
        List<String> out = new ArrayList<>();
        arr.forEach(n -> out.add(n.asText()));
        return out;
    }

    public static final class Builder {
        private final Backend backend;
        private final Store store;
        private Long maxStalenessMs;
        private long serveStaleForMs;
        private KeyMirror.StaleListener onServeStale;
        private long treeRefreshMs;
        private LongSupplier now = System::currentTimeMillis;

        private Builder(Backend backend, Store store) {
            this.backend = backend;
            this.store = store;
        }

        /** Answer "not allowed" ({@code code = "stale"}) when no sync completed in this many ms. Unset = no check (webhook-only mirrors). */
        public Builder maxStalenessMs(long ms) {
            this.maxStalenessMs = ms;
            return this;
        }

        /** Keep answering from the last data this many ms past the bound; such answers carry {@code stale = true}. */
        public Builder serveStaleForMs(long ms, KeyMirror.StaleListener listener) {
            this.serveStaleForMs = ms;
            this.onServeStale = listener;
            return this;
        }

        /** Re-pull the types and the tree on a sync only when this long has passed (default 0 = every sync). */
        public Builder treeRefreshMs(long ms) {
            this.treeRefreshMs = ms;
            return this;
        }

        public Builder clock(LongSupplier now) {
            this.now = now;
            return this;
        }

        public ResourceRoleMirror build() {
            return new ResourceRoleMirror(this);
        }
    }
}
