# atlas-ktor

An Atlas authentication provider for Ktor 3.

```kotlin
install(Authentication) {
    atlas {
        issuer = "https://auth.example.com"
        jwksUrl = "https://auth.example.com/.well-known/jwks.json"
        authorizedParties = listOf("https://app.example.com")
    }
}

routing {
    authenticate("atlas") {
        get("/me") { call.respondText(call.atlas!!.userId) }

        route("/billing") {
            install(AtlasRequire) { condition = ProtectCondition().permission("org:billing:read") }
            get { call.respondText("ok") }
        }
    }
}
```

The token is `Authorization: Bearer <jwt>`, else the `__session` cookie. No verified token is a 401 with a `WWW-Authenticate: Bearer` challenge; `AtlasRequire` answers 403 when the session lacks the permission or role. `call.atlas` is the `AtlasPrincipal` (`userId`, `sessionId`, `orgId`, `orgRole`, `claims`, `has(condition)`).

## OAuth access tokens

Set `accessTokenAudience` (and optionally `accessTokenRequiredScopes`) to also accept OAuth access tokens issued for your API, sent as `Authorization: Bearer`. A token that fails as a session is verified as an access token: signature, issuer, expiry, `aud` and required scopes; the token must say `token_use: access_token`. `call.atlas` then has `isAccessToken`, `scopes` and `clientId`, and `install(AtlasRequire) { scopes = listOf("orders:read") }` answers 403 for a missing scope. An access token has no organization claims, so it never passes a permission or role condition, and a session never passes a scope check. Access tokens are never read from the cookie.
