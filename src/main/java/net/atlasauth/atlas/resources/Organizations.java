package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.CreateOrganizationBody;
import net.atlasauth.atlas.models.CursorPage;
import net.atlasauth.atlas.models.DataList;
import net.atlasauth.atlas.models.DeletedObject;
import net.atlasauth.atlas.models.ListPage;
import net.atlasauth.atlas.models.OrgDomain;
import net.atlasauth.atlas.models.Organization;
import net.atlasauth.atlas.models.OrganizationEntitlements;
import net.atlasauth.atlas.models.OrganizationHierarchy;
import net.atlasauth.atlas.models.OrganizationInvitation;
import net.atlasauth.atlas.models.OrganizationMembership;
import net.atlasauth.atlas.models.OrganizationPolicy;
import net.atlasauth.atlas.models.OrganizationPolicyPatch;
import net.atlasauth.atlas.models.ReplaceOrganizationMetadataBody;
import net.atlasauth.atlas.models.UpdateOrganizationBody;

import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** The {@code /v1/organizations} resource, including its nested sub-resources. */
public final class Organizations {

    private final Transport t;
    private final Memberships memberships;
    private final Invitations invitations;
    private final Domains domains;
    private final GroupRoles groupRoles;

    public Organizations(Transport t) {
        this.t = t;
        this.memberships = new Memberships(t);
        this.invitations = new Invitations(t);
        this.domains = new Domains(t);
        this.groupRoles = new GroupRoles(t);
    }

    public CursorPage<Organization> list() {
        return list(null, null);
    }

    public CursorPage<Organization> list(Integer limit, String startingAfter) {
        Map<String, Object> q = Params.of().set("limit", limit).set("starting_after", startingAfter).map();
        return t.get("/v1/organizations", q, new TypeReference<>() {
        });
    }

    public Organization get(String id) {
        return t.get("/v1/organizations/" + enc(id), new TypeReference<>() {
        });
    }

    public Organization create(CreateOrganizationBody body) {
        return create(body, null);
    }

    public Organization create(CreateOrganizationBody body, String idempotencyKey) {
        return t.post("/v1/organizations", body, idempotencyKey, new TypeReference<>() {
        });
    }

    public Organization update(String id, UpdateOrganizationBody body) {
        return t.patch("/v1/organizations/" + enc(id), body, new TypeReference<>() {
        });
    }

    public DeletedObject delete(String id) {
        return t.delete("/v1/organizations/" + enc(id), new TypeReference<>() {
        });
    }

    /** {@code PUT /v1/organizations/:id/metadata} — replaces the named bags wholesale. */
    public Organization updateMetadata(String id, ReplaceOrganizationMetadataBody body) {
        return t.put("/v1/organizations/" + enc(id) + "/metadata", body, new TypeReference<>() {
        });
    }

    public OrganizationPolicy updatePolicy(String id, OrganizationPolicyPatch body) {
        return t.patch("/v1/organizations/" + enc(id) + "/policy", body, new TypeReference<>() {
        });
    }

    /** {@code PUT /v1/organizations/:id/parent} — set (or clear, with null) the parent org. */
    public Organization setParent(String id, String parentOrganizationId) {
        Map<String, Object> body = Params.of().set("parent_organization_id", parentOrganizationId).map();
        // parent_organization_id: null is a legitimate "clear" — send it explicitly.
        if (parentOrganizationId == null) {
            body = new java.util.HashMap<>();
            body.put("parent_organization_id", null);
        }
        return t.put("/v1/organizations/" + enc(id) + "/parent", body, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/organizations/:id/hierarchy} — ancestors (nearest first) + direct children. */
    public OrganizationHierarchy hierarchy(String id) {
        return t.get("/v1/organizations/" + enc(id) + "/hierarchy", new TypeReference<>() {
        });
    }

    /** {@code GET /v1/organizations/:id/entitlements}. */
    public OrganizationEntitlements entitlements(String id) {
        return t.get("/v1/organizations/" + enc(id) + "/entitlements", new TypeReference<>() {
        });
    }

    public Memberships memberships() {
        return memberships;
    }

    public Invitations invitations() {
        return invitations;
    }

    public Domains domains() {
        return domains;
    }

    public GroupRoles groupRoles() {
        return groupRoles;
    }

    // ---- nested sub-resources ---------------------------------------------

    public static final class Memberships {
        private final Transport t;

        Memberships(Transport t) {
            this.t = t;
        }

        public ListPage<OrganizationMembership> list(String orgId) {
            return t.get("/v1/organizations/" + enc(orgId) + "/memberships", new TypeReference<>() {
            });
        }

        public JsonNode add(String orgId, String userId, String role, String idempotencyKey) {
            Map<String, Object> body = Params.of().set("user_id", userId).set("role", role).map();
            return t.post("/v1/organizations/" + enc(orgId) + "/memberships", body, idempotencyKey,
                    new TypeReference<>() {
                    });
        }

        public JsonNode update(String orgId, String userId, String role) {
            Map<String, Object> body = Params.of("role", role).map();
            return t.patch("/v1/organizations/" + enc(orgId) + "/memberships/" + enc(userId), body,
                    new TypeReference<>() {
                    });
        }

        public JsonNode remove(String orgId, String userId) {
            return t.delete("/v1/organizations/" + enc(orgId) + "/memberships/" + enc(userId),
                    new TypeReference<>() {
                    });
        }
    }

    public static final class Invitations {
        private final Transport t;

        Invitations(Transport t) {
            this.t = t;
        }

        public ListPage<OrganizationInvitation> list(String orgId) {
            return t.get("/v1/organizations/" + enc(orgId) + "/invitations", new TypeReference<>() {
            });
        }

        public JsonNode create(String orgId, String email, String role, String inviterUserId, String idempotencyKey) {
            Map<String, Object> body = Params.of()
                    .set("email", email)
                    .set("role", role)
                    .set("inviter_user_id", inviterUserId)
                    .map();
            return t.post("/v1/organizations/" + enc(orgId) + "/invitations", body, idempotencyKey,
                    new TypeReference<>() {
                    });
        }

        public JsonNode revoke(String orgId, String invitationId) {
            return t.post("/v1/organizations/" + enc(orgId) + "/invitations/" + enc(invitationId) + "/revoke",
                    null, new TypeReference<>() {
                    });
        }
    }

    public static final class Domains {
        private final Transport t;

        Domains(Transport t) {
            this.t = t;
        }

        public DataList<OrgDomain> list(String orgId) {
            return t.get("/v1/organizations/" + enc(orgId) + "/domains", new TypeReference<>() {
            });
        }

        public OrgDomain create(String orgId, String domain, Boolean autoJoin, String defaultRoleId, String idempotencyKey) {
            Map<String, Object> body = Params.of()
                    .set("domain", domain)
                    .set("auto_join", autoJoin)
                    .set("default_role_id", defaultRoleId)
                    .map();
            return t.post("/v1/organizations/" + enc(orgId) + "/domains", body, idempotencyKey,
                    new TypeReference<>() {
                    });
        }

        public OrgDomain verify(String orgId, String domainId) {
            return t.post("/v1/organizations/" + enc(orgId) + "/domains/" + enc(domainId) + "/verify", null,
                    new TypeReference<>() {
                    });
        }

        public JsonNode delete(String orgId, String domainId) {
            return t.delete("/v1/organizations/" + enc(orgId) + "/domains/" + enc(domainId),
                    new TypeReference<>() {
                    });
        }
    }

    /** Directory-group -> role grants that augment members' permissions. */
    public static final class GroupRoles {
        private final Transport t;

        GroupRoles(Transport t) {
            this.t = t;
        }

        public JsonNode grant(String orgId, String groupId, String roleId) {
            return t.put("/v1/organizations/" + enc(orgId) + "/groups/" + enc(groupId) + "/roles/" + enc(roleId),
                    null, new TypeReference<>() {
                    });
        }

        public JsonNode revoke(String orgId, String groupId, String roleId) {
            return t.delete("/v1/organizations/" + enc(orgId) + "/groups/" + enc(groupId) + "/roles/" + enc(roleId),
                    new TypeReference<>() {
                    });
        }
    }
}
