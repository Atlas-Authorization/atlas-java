package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;
import net.atlasauth.atlas.models.AddOrganizationEndUserBody;
import net.atlasauth.atlas.models.CreateOrganizationAuditStreamBody;
import net.atlasauth.atlas.models.CreateOrganizationBody;
import net.atlasauth.atlas.models.CursorPage;
import net.atlasauth.atlas.models.DataList;
import net.atlasauth.atlas.models.DeletedObject;
import net.atlasauth.atlas.models.ListEndUsersOrganizationsParams;
import net.atlasauth.atlas.models.ListPage;
import net.atlasauth.atlas.models.OrgDomain;
import net.atlasauth.atlas.models.Organization;
import net.atlasauth.atlas.models.OrganizationAuditStream;
import net.atlasauth.atlas.models.OrganizationBranding;
import net.atlasauth.atlas.models.OrganizationBrandingBody;
import net.atlasauth.atlas.models.OrganizationEndUser;
import net.atlasauth.atlas.models.OrganizationEntitlements;
import net.atlasauth.atlas.models.OrganizationHierarchy;
import net.atlasauth.atlas.models.OrganizationInvitation;
import net.atlasauth.atlas.models.OrganizationMembership;
import net.atlasauth.atlas.models.OrganizationPolicy;
import net.atlasauth.atlas.models.OrganizationPolicyPatch;
import net.atlasauth.atlas.models.OrganizationSettings;
import net.atlasauth.atlas.models.ReaderDomain;
import net.atlasauth.atlas.models.ReaderDomainSetup;
import net.atlasauth.atlas.models.ReplaceOrganizationMetadataBody;
import net.atlasauth.atlas.models.UpdateOrganizationBody;

import java.util.List;
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

    /** {@code GET /v1/organizations/{id}/branding} — Read an organization's hosted-page branding overlay (display name, logo, primary colour, support URL, custom domain). */
    public OrganizationBranding getBranding(String id) {
        return t.get("/v1/organizations/" + enc(id) + "/branding", new TypeReference<>() {
        });
    }

    /** {@code PUT /v1/organizations/{id}/branding} — Set an organization's hosted-page branding overlay — a partial merge: a provided field overrides the instance brand, null (or empty string) clears it back. */
    public OrganizationBranding replaceBranding(String id, OrganizationBrandingBody body) {
        return t.put("/v1/organizations/" + enc(id) + "/branding", body, new TypeReference<>() {
        });
    }

    /** {@code GET /v1/organizations/{orgId}/reader_domain} — List the organization's reader domain(s) — the host(s) whose sign-ups join this org's end-user pool, with DNS/certificate status. */
    public ListPage<ReaderDomain> listReaderDomains(String orgId) {
        return t.get("/v1/organizations/" + enc(orgId) + "/reader_domain", new TypeReference<>() {
        });
    }

    /** {@code POST /v1/organizations/{orgId}/reader_domain} — Bind a host to this organization's reader pool: a sign-up on it joins the pool. */
    public ReaderDomainSetup setReaderDomain(String orgId, String host) {
        Map<String, Object> payload = Params.of().set("host", host).map();
        return t.post("/v1/organizations/" + enc(orgId) + "/reader_domain", payload, new TypeReference<>() {
        });
    }

    /** {@code DELETE /v1/organizations/{orgId}/reader_domain} — Remove the organization's reader domain binding and drop its edge router. */
    public DeletedObject removeReaderDomain(String orgId) {
        return t.delete("/v1/organizations/" + enc(orgId) + "/reader_domain", new TypeReference<>() {
        });
    }

    public CursorPage<OrganizationEndUser> listEndUsers(String orgId) {
        return listEndUsers(orgId, null);
    }

    /** {@code GET /v1/organizations/{orgId}/end_users} — List an organization's end-user (reader) pool — the B2B2C tier of external users attached to the org, distinct from its staff members. */
    public CursorPage<OrganizationEndUser> listEndUsers(String orgId, ListEndUsersOrganizationsParams params) {
        return t.get("/v1/organizations/" + enc(orgId) + "/end_users", params == null ? null : params.toQuery(), new TypeReference<>() {
        });
    }

    /** {@code POST /v1/organizations/{orgId}/end_users} — Attach an instance user to an organization's end-user pool as a reader (the token-exchange path). */
    public OrganizationEndUser addEndUser(String orgId, AddOrganizationEndUserBody body) {
        return t.post("/v1/organizations/" + enc(orgId) + "/end_users", body, new TypeReference<>() {
        });
    }

    /** As above, with an {@code Idempotency-Key} so a retry replays the first response. */
    public OrganizationEndUser addEndUser(String orgId, AddOrganizationEndUserBody body, String idempotencyKey) {
        return t.post("/v1/organizations/" + enc(orgId) + "/end_users", body, idempotencyKey, new TypeReference<>() {
        });
    }

    /** {@code PATCH /v1/organizations/{orgId}/end_users/{userId}} — Approve (active), gate (pending) or block a reader in an organization end-user pool. */
    public OrganizationEndUser updateEndUser(String orgId, String userId, String status) {
        Map<String, Object> payload = Params.of().set("status", status).map();
        return t.patch("/v1/organizations/" + enc(orgId) + "/end_users/" + enc(userId), payload, new TypeReference<>() {
        });
    }

    /** {@code DELETE /v1/organizations/{orgId}/end_users/{userId}} — Remove a reader from an organization's end-user pool. */
    public DeletedObject removeEndUser(String orgId, String userId) {
        return t.delete("/v1/organizations/" + enc(orgId) + "/end_users/" + enc(userId), new TypeReference<>() {
        });
    }

    /** {@code GET /v1/organizations/{id}/audit_streams} — List an organization's registered audit stream destinations. */
    public ListPage<OrganizationAuditStream> listAuditStreams(String id) {
        return t.get("/v1/organizations/" + enc(id) + "/audit_streams", new TypeReference<>() {
        });
    }

    /** {@code POST /v1/organizations/{id}/audit_streams} — Register an audit stream destination for one organization: an HTTPS SIEM/collector URL plus a shared HMAC signing secret (write-only, stored encrypted, min 16 chars) and an optional format hint. */
    public OrganizationAuditStream createAuditStream(String id, CreateOrganizationAuditStreamBody body) {
        return t.post("/v1/organizations/" + enc(id) + "/audit_streams", body, new TypeReference<>() {
        });
    }

    /** As above, with an {@code Idempotency-Key} so a retry replays the first response. */
    public OrganizationAuditStream createAuditStream(String id, CreateOrganizationAuditStreamBody body, String idempotencyKey) {
        return t.post("/v1/organizations/" + enc(id) + "/audit_streams", body, idempotencyKey, new TypeReference<>() {
        });
    }

    /** {@code DELETE /v1/organizations/{id}/audit_streams/{streamId}} — Delete one of an organization's audit stream destinations. */
    public DeletedObject deleteAuditStream(String id, String streamId) {
        return t.delete("/v1/organizations/" + enc(id) + "/audit_streams/" + enc(streamId), new TypeReference<>() {
        });
    }

    /** {@code GET /v1/organizations/{id}/settings} — Fetch one organization's settings values. */
    public OrganizationSettings getSettings(String id) {
        return t.get("/v1/organizations/" + enc(id) + "/settings", new TypeReference<>() {
        });
    }

    /** {@code PUT /v1/organizations/{id}/settings} — Write one organization's settings values. */
    public OrganizationSettings replaceSettings(String id, Map<String, Object> values) {
        Map<String, Object> payload = Params.of().set("values", values).map();
        return t.put("/v1/organizations/" + enc(id) + "/settings", payload, new TypeReference<>() {
        });
    }

    /** {@code PATCH /v1/organizations/{id}/settings} — Merge a partial object into an organization's settings values (RFC 7396 shallow merge): a key set to null is REMOVED, any other value sets/overwrites it, untouched keys are preserved — so a single settings key can be removed or changed without… */
    public OrganizationSettings updateSettings(String id, Map<String, Object> values) {
        Map<String, Object> payload = Params.of().set("values", values).map();
        return t.patch("/v1/organizations/" + enc(id) + "/settings", payload, new TypeReference<>() {
        });
    }
}
