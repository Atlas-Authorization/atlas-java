package net.atlasauth.atlas.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/** Body for {@code POST /v1/ldap_server_clients}. Required: name, baseDn, bindCn. */
public final class LdapServerClientsCreateBody {
    /** Required. */
    public String name;
    /** e.g. dc=acme,dc=com Required. */
    public String baseDn;
    /** The service account cn under base_dn. Required. */
    public String bindCn;
    /** Source networks allowed to bind (empty = anywhere). */
    public List<String> allowedCidrs;
    public Boolean allowUserBind;
    public Long sizeLimit;
    public String status;

    public LdapServerClientsCreateBody() {
    }

    public LdapServerClientsCreateBody name(String v) { this.name = v; return this; }
    public LdapServerClientsCreateBody baseDn(String v) { this.baseDn = v; return this; }
    public LdapServerClientsCreateBody bindCn(String v) { this.bindCn = v; return this; }
    public LdapServerClientsCreateBody allowedCidrs(List<String> v) { this.allowedCidrs = v; return this; }
    public LdapServerClientsCreateBody allowUserBind(Boolean v) { this.allowUserBind = v; return this; }
    public LdapServerClientsCreateBody sizeLimit(Long v) { this.sizeLimit = v; return this; }
    public LdapServerClientsCreateBody status(String v) { this.status = v; return this; }
}
