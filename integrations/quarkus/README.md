# atlas-quarkus

Atlas session authentication for Quarkus (Jakarta REST).

```properties
atlas.issuer=https://auth.example.com
atlas.jwks-url=https://auth.example.com/.well-known/jwks.json
atlas.authorized-parties=https://app.example.com
```

```java
@Path("/orders")
@AtlasAuthenticated
public class OrdersResource {

    @GET
    public String mine(@Context ContainerRequestContext request) {
        return AtlasContext.claims(request).sub();
    }

    @DELETE
    @AtlasRequires(permission = "org:orders:delete")
    public void delete() { }
}
```

`@AtlasAuthenticated` guards a class or method: `Authorization: Bearer`, else the `__session` cookie, is verified locally; anything else is a 401. `@AtlasRequires` adds `role`, `permission`, `anyPermission`, `allPermissions`, `appRole` and `appPermission` checks (403 when they fail). The JAX-RS `SecurityContext` principal is the user id and `isUserInRole` answers for the organization role, app roles and permissions. Declare your own `SessionVerifier` bean to replace the configured one. Verification may fetch the key set (at most once a minute); on a reactive endpoint annotate the resource `@Blocking` if you want that off the event loop.

## OAuth access tokens

Set `atlas.access-token.audience` (and optionally `atlas.access-token.required-scopes`) to also accept OAuth access tokens issued for your API, sent as `Authorization: Bearer`. A token that fails as a session is verified as an access token (signature, issuer, expiry, `aud`, required scopes, and `token_use: access_token`). Guard scopes with `@AtlasRequires(scopes = "orders:read")` (403 when missing); read the result with `AtlasContext.accessToken(request)`; `isUserInRole` answers for the scopes. An access token never passes a permission or role rule, and a session never passes a scope rule. Access tokens are never read from the cookie.
