package net.atlasauth.atlas.webhook;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

/**
 * Webhook event names: the canonical spelling of every event, the deprecated aliases an endpoint that lists one
 * by name still receives (with the same {@code atlas-id} as the canonical event) until the sunset date, and the
 * events added with endpoint health, secret keys, the data-rights executor and the status page.
 */
public final class WebhookEvents {

    private WebhookEvents() {
    }

    /**
     * A deprecated event spelling.
     *
     * @param canonical the event to subscribe to instead
     * @param kind      {@code rename} (sent whenever the canonical event is) or {@code companion} (only for some occurrences)
     */
    public record Alias(String canonical, String kind) {
    }

    /** Every deprecated event spelling, mapped to its canonical event. */
    public static final Map<String, Alias> ALIASES;

    static {
        Map<String, Alias> m = new LinkedHashMap<>();
        m.put("session.ended", new Alias("session.revoked", "companion"));
        m.put("session.removed", new Alias("session.revoked", "companion"));
        m.put("organizationMembership.created", new Alias("organization_membership.created", "rename"));
        m.put("organizationMembership.updated", new Alias("organization_membership.updated", "rename"));
        m.put("organizationMembership.deleted", new Alias("organization_membership.deleted", "rename"));
        m.put("organization.membership.deleted", new Alias("organization_membership.deleted", "rename"));
        m.put("organizationInvitation.created", new Alias("organization_invitation.created", "rename"));
        m.put("organizationInvitation.accepted", new Alias("organization_invitation.accepted", "rename"));
        m.put("organizationInvitation.revoked", new Alias("organization_invitation.revoked", "rename"));
        m.put("organizationInvitation.declined", new Alias("organization_invitation.declined", "rename"));
        m.put("organizationInvitation.resent", new Alias("organization_invitation.resent", "rename"));
        m.put("teamMembership.created", new Alias("team_membership.created", "rename"));
        m.put("teamMembership.deleted", new Alias("team_membership.deleted", "rename"));
        m.put("appRole.created", new Alias("app_role.created", "rename"));
        m.put("appRole.updated", new Alias("app_role.updated", "rename"));
        m.put("appRole.deleted", new Alias("app_role.deleted", "rename"));
        m.put("organizationDomain.created", new Alias("organization_domain.created", "rename"));
        m.put("organizationDomain.updated", new Alias("organization_domain.updated", "rename"));
        m.put("organizationDomain.deleted", new Alias("organization_domain.deleted", "rename"));
        m.put("organization.settings.updated", new Alias("organization_settings.updated", "rename"));
        m.put("waitlistEntry.created", new Alias("waitlist_entry.created", "rename"));
        m.put("waitlistEntry.updated", new Alias("waitlist_entry.updated", "rename"));
        m.put("billing.subscription.created", new Alias("billing_subscription.created", "rename"));
        m.put("billing.subscription.updated", new Alias("billing_subscription.updated", "rename"));
        m.put("billing.subscription.canceled", new Alias("billing_subscription.canceled", "rename"));
        m.put("billing.invoice.payment_failed", new Alias("billing_invoice.payment_failed", "rename"));
        m.put("billing.trial.started", new Alias("billing_trial.started", "rename"));
        m.put("billing.trial.will_end", new Alias("billing_trial.will_end", "rename"));
        m.put("billing.trial.ended", new Alias("billing_trial.ended", "rename"));
        m.put("entitlement.override.created", new Alias("entitlement_override.created", "rename"));
        m.put("entitlement.override.deleted", new Alias("entitlement_override.deleted", "rename"));
        m.put("entitlement.override.expired", new Alias("entitlement_override.expired", "rename"));
        m.put("scimToken.created", new Alias("scim_token.created", "rename"));
        m.put("scimToken.revoked", new Alias("scim_token.revoked", "rename"));
        m.put("ssoConnection.created", new Alias("sso_connection.created", "rename"));
        m.put("ssoConnection.updated", new Alias("sso_connection.updated", "rename"));
        m.put("ssoConnection.deleted", new Alias("sso_connection.deleted", "rename"));
        m.put("user.data_export.requested", new Alias("user.data_export_requested", "rename"));
        m.put("user.deletion_cancelled", new Alias("user.deletion_canceled", "companion"));
        ALIASES = Collections.unmodifiableMap(m);
    }

    /** Whether {@code name} is a deprecated spelling. */
    public static boolean isDeprecated(String name) {
        return ALIASES.containsKey(name);
    }

    /** The canonical name for any spelling (an unknown name is returned unchanged). */
    public static String canonical(String name) {
        Alias a = ALIASES.get(name);
        return a != null ? a.canonical() : name;
    }

    /** Every name an event is known by: the canonical one, then its deprecated aliases (sorted). */
    public static List<String> spellings(String name) {
        String canonical = canonical(name);
        TreeSet<String> aliases = new TreeSet<>();
        for (Map.Entry<String, Alias> e : ALIASES.entrySet()) {
            if (e.getValue().canonical().equals(canonical)) {
                aliases.add(e.getKey());
            }
        }
        List<String> out = new ArrayList<>();
        out.add(canonical);
        out.addAll(aliases);
        return out;
    }

    /** An endpoint's health changed (also sent as email to its alert contacts). */
    public static final String WEBHOOK_ENDPOINT_FAILING = "webhook_endpoint.failing";
    public static final String WEBHOOK_ENDPOINT_DISABLED = "webhook_endpoint.disabled";
    public static final String WEBHOOK_ENDPOINT_RECOVERED = "webhook_endpoint.recovered";
    /** The same for any outbound target (referral connection, back-channel receiver, integration). */
    public static final String DELIVERY_TARGET_FAILING = "delivery_target.failing";
    public static final String DELIVERY_TARGET_DISABLED = "delivery_target.disabled";
    public static final String DELIVERY_TARGET_RECOVERED = "delivery_target.recovered";
    /** The synthetic test event. */
    public static final String WEBHOOK_TEST = "webhook.test";
    /** A secret key is about to expire ({@code data} carries {@code days_left} and {@code threshold_days}). */
    public static final String SECRET_KEY_EXPIRING = "secret_key.expiring";
    /** A secret key was revoked (audience security). */
    public static final String SECRET_KEY_REVOKED = "secret_key.revoked";
    /** A privacy request moved forward / finished; {@code data} is the same request the GET returns. */
    public static final String PRIVACY_REQUEST_UPDATED = "privacy_request.updated";
    public static final String PRIVACY_REQUEST_COMPLETED = "privacy_request.completed";
    /** An account deletion was scheduled / canceled inside its grace window. */
    public static final String USER_DELETION_SCHEDULED = "user.deletion_scheduled";
    public static final String USER_DELETION_CANCELED = "user.deletion_canceled";
    /** A status-page incident was created / updated / resolved. */
    public static final String STATUS_INCIDENT_CREATED = "status.incident.created";
    public static final String STATUS_INCIDENT_UPDATED = "status.incident.updated";
    public static final String STATUS_INCIDENT_RESOLVED = "status.incident.resolved";

    /** Scope that lets a key reveal webhook signing secrets. */
    public static final String SCOPE_WEBHOOKS_REVEAL = "webhooks:reveal";
    /** Scope that lets a key call {@code /v1/privacy/*}. */
    public static final String SCOPE_PRIVACY_EXECUTE = "privacy:execute";
    /** Scope that lets an erasure also revoke the Sign in with Atlas grant. */
    public static final String SCOPE_PRIVACY_GRANTS_REVOKE = "privacy:grants:revoke";
}
