package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code PATCH /v1/ldap_server_clients/{id}}. */
public final class LdapServerClientsUpdateBody {
    public String name;
    /** e.g. dc=acme,dc=com */
    public String baseDn;
    /** The service account cn under base_dn. */
    public String bindCn;
    /** Source networks allowed to bind (empty = anywhere). */
    public List<String> allowedCidrs;
    public Boolean allowUserBind;
    public Long sizeLimit;
    public String status;

    public LdapServerClientsUpdateBody() {
    }

    public LdapServerClientsUpdateBody name(String v) { this.name = v; return this; }
    public LdapServerClientsUpdateBody baseDn(String v) { this.baseDn = v; return this; }
    public LdapServerClientsUpdateBody bindCn(String v) { this.bindCn = v; return this; }
    public LdapServerClientsUpdateBody allowedCidrs(List<String> v) { this.allowedCidrs = v; return this; }
    public LdapServerClientsUpdateBody allowUserBind(Boolean v) { this.allowUserBind = v; return this; }
    public LdapServerClientsUpdateBody sizeLimit(Long v) { this.sizeLimit = v; return this; }
    public LdapServerClientsUpdateBody status(String v) { this.status = v; return this; }
}
