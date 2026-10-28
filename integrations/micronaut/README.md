# atlas-micronaut

Atlas session tokens for Micronaut Security 4.

```yaml
atlas:
  issuer: https://auth.example.com
  jwks-url: https://auth.example.com/.well-known/jwks.json
  authorized-parties: [https://app.example.com]
```

```java
@Controller("/orders")
@Secured(SecurityRule.IS_AUTHENTICATED)
class OrdersController {

    @Get
    String mine(Authentication authentication) {
        return authentication.getName(); // the Atlas user id
    }

    @Delete
    @Secured("org:orders:delete") // an organization permission, an org role or an app role
    void delete() { }
}
```

`AtlasTokenReader` reads `Authorization: Bearer`, else the `__session` cookie (`atlas.cookie-name`); `AtlasTokenValidator` verifies it locally and builds the `Authentication`: name = user id, roles = organization role, app roles, organization and app permissions, attributes `sid`, `org_id`, `org_role` and `atlas.claims` (the full `SessionClaims`). Anything unverified is a 401. Declare your own `SessionVerifier` bean to replace the configured one.

## OAuth access tokens

Set `atlas.access-token.audience` (and optionally `atlas.access-token.required-scopes`) to also accept OAuth access tokens issued for your API, sent as `Authorization: Bearer`. A token that fails as a session is verified as an access token (signature, issuer, expiry, `aud`, required scopes, and `token_use: access_token`). The authentication's name is the token's subject, its roles are the scopes (so `@Secured("orders:read")` works), and the attributes include `client_id` and `atlas.accessToken`. Access tokens are never read from the cookie.
