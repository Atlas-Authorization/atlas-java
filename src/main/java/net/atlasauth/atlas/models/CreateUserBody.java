package net.atlasauth.atlas.models;

import java.util.Map;

/**
 * Body for {@code POST /v1/users}. Only {@code emailAddress} is required; unset
 * fields are omitted from the request. Fluent setters return {@code this}.
 */
public final class CreateUserBody {
    public String emailAddress;
    public String password;
    public String firstName;
    public String lastName;
    public Boolean emailVerified;
    public Map<String, Object> publicMetadata;
    public Map<String, Object> privateMetadata;
    public Map<String, Object> unsafeMetadata;

    public CreateUserBody() {
    }

    public CreateUserBody(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public CreateUserBody emailAddress(String v) { this.emailAddress = v; return this; }
    public CreateUserBody password(String v) { this.password = v; return this; }
    public CreateUserBody firstName(String v) { this.firstName = v; return this; }
    public CreateUserBody lastName(String v) { this.lastName = v; return this; }
    public CreateUserBody emailVerified(Boolean v) { this.emailVerified = v; return this; }
    public CreateUserBody publicMetadata(Map<String, Object> v) { this.publicMetadata = v; return this; }
    public CreateUserBody privateMetadata(Map<String, Object> v) { this.privateMetadata = v; return this; }
    public CreateUserBody unsafeMetadata(Map<String, Object> v) { this.unsafeMetadata = v; return this; }
}
