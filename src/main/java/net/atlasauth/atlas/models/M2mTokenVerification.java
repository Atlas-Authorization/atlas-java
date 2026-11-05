package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record M2mTokenVerification(
        String object,
        boolean valid,
        String machineId,
        String name,
        /** Machine JWT only: the resource server it is bound to. */ String audience,
        /** Machine JWT only: the scopes it carries. */ List<String> scopes,
        /** Machine JWT only: its organization context. */ String organizationId,
        String orgRole,
        List<String> orgPermissions,
        /** Machine JWT only: when it expires (epoch ms). */ Long expiresAt,
        /** The resource servers the machine holds grants on now. */ List<Map<String, Object>> grants,
        /** The organizations it is a member of now. */ List<Map<String, Object>> organizationMemberships) {

    /** The shape before machine JWTs. */
    public M2mTokenVerification(String object, boolean valid, String machineId, String name) {
        this(object, valid, machineId, name, null, null, null, null, null, null, null, null);
    }
}
