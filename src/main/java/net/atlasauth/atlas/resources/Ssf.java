package net.atlasauth.atlas.resources;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import net.atlasauth.atlas.Params;
import net.atlasauth.atlas.http.Transport;

import java.util.List;
import java.util.Map;

import static net.atlasauth.atlas.http.Transport.enc;

/** The {@code Ssf} resources. */
public final class Ssf {

    private final Transport t;

    public Ssf(Transport t) {
        this.t = t;
    }

    /** The {@code streams} sub-resource. */
    public SsfStreams streams() {
        return new SsfStreams(t);
    }

    /** The {@code receivers} sub-resource. */
    public SsfReceivers receivers() {
        return new SsfReceivers(t);
    }
}
