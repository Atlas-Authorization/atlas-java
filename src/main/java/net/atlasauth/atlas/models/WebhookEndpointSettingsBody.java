package net.atlasauth.atlas.models;

import java.util.List;
import java.util.Map;

/**
 * The webhook endpoint fields shared by create, update and upsert-by-URL: naming, signature versions,
 * alert contacts, an event destination and the provisioning tags.
 *
 * @param <T> the concrete body, for fluent chaining
 */
public abstract class WebhookEndpointSettingsBody<T extends WebhookEndpointSettingsBody<T>> extends ExtensibleBody {
    /** {@code canonical} or {@code legacy}: which names {@code "*"} delivers under. */
    public String eventNaming;
    /** A third-party receiver: preflight does not flag its host. */
    public Boolean external;
    /** {@code v1} and/or {@code v2} (v2 also signs the URL). */
    public List<String> signatureVersions;
    /** Up to 20 addresses emailed when the endpoint fails; empty leaves the default (the account's owners). */
    public List<String> alertContacts;
    public String description;
    /** An event destination instead of an HTTPS URL: {@code type} aws_eventbridge, gcp_pubsub or cloudflare_queue plus its settings. */
    public Map<String, Object> destination;
    /** Your own credentials for the destination (stored encrypted, write-only). */
    public Map<String, Object> destinationCredentials;
    /** A stable key of your own: a repeat create with it returns the existing endpoint. */
    public String externalRef;
    /** Provisioning tags (strings, numbers and booleans). */
    public Map<String, Object> metadata;

    protected abstract T self();

    public T eventNaming(String v) { this.eventNaming = v; return self(); }
    public T external(Boolean v) { this.external = v; return self(); }
    public T signatureVersions(List<String> v) { this.signatureVersions = v; return self(); }
    public T alertContacts(List<String> v) { this.alertContacts = v; return self(); }
    public T description(String v) { this.description = v; return self(); }
    public T destination(Map<String, Object> v) { this.destination = v; return self(); }
    public T destinationCredentials(Map<String, Object> v) { this.destinationCredentials = v; return self(); }
    public T externalRef(String v) { this.externalRef = v; return self(); }
    public T metadata(Map<String, Object> v) { this.metadata = v; return self(); }

    /**
     * Set any field by its wire name, including an explicit {@code null} (which
     * clears a stored value on a partial update) or a field this SDK does not model yet.
     */
    public T set(String key, Object value) { putField(key, value); return self(); }
}
