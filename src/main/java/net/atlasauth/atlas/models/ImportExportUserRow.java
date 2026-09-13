package net.atlasauth.atlas.models;

import java.util.List;

/** One user row accepted by an import. Provide {@code password} or a {@code passwordHash}. */
public final class ImportExportUserRow {
    public String emailAddress;
    public String email;
    public String password;
    public String passwordHash;
    public Boolean emailVerified;
    public Boolean verified;
    public String firstName;
    public String lastName;
    public String username;
    public Object publicMetadata;
    public Object privateMetadata;
    public List<ImportExportExternalAccount> externalAccounts;

    public ImportExportUserRow() {
    }

    public ImportExportUserRow emailAddress(String v) { this.emailAddress = v; return this; }
    public ImportExportUserRow email(String v) { this.email = v; return this; }
    public ImportExportUserRow password(String v) { this.password = v; return this; }
    public ImportExportUserRow passwordHash(String v) { this.passwordHash = v; return this; }
    public ImportExportUserRow emailVerified(Boolean v) { this.emailVerified = v; return this; }
    public ImportExportUserRow verified(Boolean v) { this.verified = v; return this; }
    public ImportExportUserRow firstName(String v) { this.firstName = v; return this; }
    public ImportExportUserRow lastName(String v) { this.lastName = v; return this; }
    public ImportExportUserRow username(String v) { this.username = v; return this; }
    public ImportExportUserRow publicMetadata(Object v) { this.publicMetadata = v; return this; }
    public ImportExportUserRow privateMetadata(Object v) { this.privateMetadata = v; return this; }
    public ImportExportUserRow externalAccounts(List<ImportExportExternalAccount> v) { this.externalAccounts = v; return this; }
}
