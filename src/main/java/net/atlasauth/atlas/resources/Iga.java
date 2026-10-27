package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;

import java.util.List;
import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** The {@code Iga} resources. */
public final class Iga {

    private final Transport t;

    public Iga(Transport t) {
        this.t = t;
    }

    /** The {@code entitlements} sub-resource. */
    public IgaEntitlements entitlements() {
        return new IgaEntitlements(t);
    }

    /** The {@code requests} sub-resource. */
    public IgaRequests requests() {
        return new IgaRequests(t);
    }

    /** The {@code grants} sub-resource. */
    public IgaGrants grants() {
        return new IgaGrants(t);
    }

    /** The {@code delegations} sub-resource. */
    public IgaDelegations delegations() {
        return new IgaDelegations(t);
    }

    /** The {@code sod} sub-resource. */
    public IgaSod sod() {
        return new IgaSod(t);
    }

    /** The {@code lifecycle} sub-resource. */
    public IgaLifecycle lifecycle() {
        return new IgaLifecycle(t);
    }

    /** The {@code campaigns} sub-resource. */
    public IgaCampaigns campaigns() {
        return new IgaCampaigns(t);
    }
}
