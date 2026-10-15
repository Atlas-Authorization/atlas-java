# Atlas Java SDK

The official Java backend SDK for [Atlas](https://atlasauth.net) — a typed client
for the Backend API plus local, network-free session-token verification.

It is the Java peer of `@atlas/backend`: the same secret-key management surface
(`AtlasClient`) and the same `§7.3` session verifier (`SessionVerifier`), built on
nothing but the JDK `HttpClient`, Jackson, and Nimbus JOSE — no framework
required, so it runs the same in a plain `main`, a servlet container, or Spring
Boot.

- **Java 17+**
- Management client covering every Backend API and platform operation in the public API reference
- Local JWT verification against cached JWKS — no Atlas round-trip on your hot path
- Typed, catchable errors (`AtlasApiException` and status-specific subclasses)

## Install

Maven:

```xml
<dependency>
  <groupId>net.atlasauth</groupId>
  <artifactId>atlas-java</artifactId>
  <version>1.3.1</version>
</dependency>
```

Gradle:

```kotlin
implementation("net.atlasauth:atlas-java:1.3.1")
```

## The management client

Build one `AtlasClient` and share it — it is immutable and thread-safe. Only the
secret key is required.

```java
import net.atlasauth.atlas.AtlasClient;
import net.atlasauth.atlas.models.User;

AtlasClient atlas = AtlasClient.builder()
        .secretKey(System.getenv("ATLAS_SECRET_KEY")) // sk_...
        .build();

User user = atlas.users().get("usr_123");

User created = atlas.users().create(
        new net.atlasauth.atlas.models.CreateUserBody("ada@example.com")
                .firstName("Ada")
                .lastName("Lovelace"));
```

Every namespace hangs off the client — `atlas.organizations()`, `atlas.roles()`,
`atlas.sessions()`, `atlas.oauthClients()`, `atlas.webhooks()`,
`atlas.instanceSecurity()`, `atlas.machines()`, `atlas.design()`, `atlas.platform()`, and so on.

### Configuration

```java
AtlasClient atlas = AtlasClient.builder()
        .secretKey(secret)
        .apiUrl("https://api.atlasauth.net")        // default; override per instance
        .timeout(java.time.Duration.ofSeconds(15))
        .httpClient(myTunedHttpClient)          // optional: proxy, pooling
        .build();
```

### Idempotency

Mutating creates accept an optional idempotency key (`§9.1`):

```java
atlas.users().create(body, "idem-" + requestId);
```

### Pagination

Cursor-paged routes return a `CursorPage<T>` (`data`, `hasMore`, `nextCursor`);
pass `nextCursor` back as `startingAfter` to walk pages.

```java
String cursor = null;
do {
    CursorPage<User> page = atlas.users().list(100, cursor);
    page.data().forEach(::process);
    cursor = page.hasMore() ? page.nextCursor() : null;
} while (cursor != null);
```

### Strict request bodies

By default a write that includes a field the endpoint does not accept still
succeeds, and the server lists the ignored fields in an `Atlas-Unknown-Fields`
response header. The SDK reports those through a callback (by default a
`WARNING` on the `net.atlasauth.atlas` `System.Logger`):

```java
AtlasClient atlas = AtlasClient.builder()
        .secretKey(secret)
        .onUnknownFields(w -> log.warn("ignored {} on {} {}", w.fields(), w.method(), w.path()))
        .build();
```

Turn on strict mode to get a `422` with code `UNKNOWN_FIELD` instead, before
anything changes. Set it for the whole client, or per call:

```java
AtlasClient strict = AtlasClient.builder().secretKey(secret).strict(true).build();

atlas.withStrict(true).users().update("usr_123", body);   // just this call
atlas.withStrict(false).users().update("usr_123", body);  // opt out when the instance defaults to strict
```

### Clearing a field

Unset fields are never sent. To clear a stored value on a partial update, send an
explicit `null` with `set(...)`, which every request body has (and which also
reaches fields this SDK version does not model yet):

```java
atlas.design().update(new AtlasDesign().set("paletteDark", null));
atlas.organizations().updatePolicy("org_1", new OrganizationPolicyPatch().clear("ipAllowlist"));
```

### Platform API (platform key)

Provisioning apps and registering "Sign in with Atlas" apps uses a platform key
(`psk_...`). Give the builder one (alone, or next to a secret key) and use
`atlas.platform()`:

```java
AtlasClient atlas = AtlasClient.builder()
        .platformKey(System.getenv("ATLAS_PLATFORM_KEY"))
        .build();

AtlasIdAppWithSecret app = atlas.platform().atlasIdApps().create(new CreateAtlasIdAppBody()
        .name("Acme")
        .redirectUris(List.of("https://acme.example/callback"))
        .postLogoutRedirectUris(List.of("https://acme.example/"))
        .backchannelLogoutUri("https://acme.example/logout/backchannel")
        .branding(new AtlasIdAppBrandingInput()
                .accentColor("#4f46e5")
                .copy(new AtlasIdAppCopy().signInTitle("Sign in to {app}"))));

String clientSecret = app.clientSecret(); // shown once; rotateSecret(id) issues a new one
```

`atlas.platform().apps()` provisions and manages instances, and
`atlas.platform().designAssets()` holds images for app designs.

### Sign-in page design

`atlas.design()` reads and changes the instance design (`get`, `replace`,
`update`, `preview`), an organization's override (`getForOrganization`, …), and
the public `catalog()` of presets and fonts. The `AtlasDesign` object is fully
typed (palette, typography, shape, card, layout, buttons, inputs, background,
logo), and wire keys keep their camelCase.

```java
DesignSettings d = atlas.design().update(new AtlasDesign()
        .preset("glass")
        .palette(new AtlasDesignPalette().primary("#4f46e5")));
d.warnings().forEach(w -> System.out.println(w.message()));
```

Images are uploaded as raw bytes with their content type:

```java
byte[] png = Files.readAllBytes(Path.of("logo.png"));
DesignAsset logo = atlas.design().uploadAsset(png, "image/png", "logo");
atlas.design().update(new AtlasDesign().logo(new AtlasDesignLogo().assetId(logo.assetId())));
```

### Setup checks and more

- `atlas.instance().preflight()` lists configuration problems, each with a
  `severity`, `code`, `message`, `fix` and `subject`.
- `atlas.instance().getCookieConsent()` / `replaceCookieConsent(...)` /
  `deleteCookieConsent()` manage the cookie-consent script on the hosted pages,
  including an optional "Cookie settings" link (a preset or your own attributes):

  ```java
  atlas.instance().replaceCookieConsent(new CookieConsentBody()
          .scriptUrl("https://consent.cookiebot.com/uc.js")
          .siteId("your-cbid")
          .settingsLink(new CookieConsentSettingsLinkInput("Cookie settings")
                  .preset(CookieConsentSettingsLink.PRESET_COOKIEBOT)));
  ```

  The response's `settingsLink().renderedAttributes()` shows the attributes the link
  carries: yours plus the preset's trigger.
- `atlas.messaging().deleteChannel("email")` is refused with `ConflictException`
  (`EMAIL_PROVIDER_IN_USE`, `meta.features` lists the features that need email) while
  nothing else would deliver email; `deleteChannel("email", true)` removes it anyway.
- `CreateOAuthClientBody` / `UpdateOAuthClientBody.skipLogoutConfirmation(true)` (first-party
  clients only) lets logout from that client with an exactly registered post-logout redirect
  URI return straight to your app without the confirmation page.
- `atlas.oauthProviders().list(List.of("google"), true)` filters the catalog; the
  upsert body takes `displayName`, `iconUrl`, `mode`, and wallet settings
  (`UpsertOAuthProviderBody.siwe(domain, chainIds, rpcUrl)`, `siws(...)`, `siwf(...)`).
- `atlas.webhooks().endpoints()` updates endpoints, reveals or rotates the signing
  secret and sends a test event; `atlas.webhooks().redeliver(id, deliveryId)`
  retries a past delivery.
- `atlas.entitlementOverrides()` grants a plan, features or limits to a user or
  organization outside of billing.

### API key spend caps

Give an end-user API key caps in your own units, then meter what your API consumed
against them. A refused debit records nothing and is not an exception, so you
enforce it inside your own transaction:

```java
ApiKeyWithSecret key = atlas.apiKeys().create(new CreateApiKeyBody("user", "user_123")
        .name("CI")
        .limits(List.of(
                new ApiKeyLimit("requests", 10_000, ApiKeyLimit.PERIOD_DAY),        // UTC calendar day
                new ApiKeyLimit("usd_cents", 500, ApiKeyLimit.PERIOD_REQUEST))));  // caps one debit

ApiKeyUsageDebit debit = atlas.apiKeys().recordUsage(key.id(),
        new RecordApiKeyUsageBody(1, "requests").idempotencyKey(requestId));
if (!debit.allowed()) {                      // debit.code() == ErrorCodes.LIMIT_EXCEEDED
    return ResponseEntity.status(429).build();
}

ApiKeyUsage usage = atlas.apiKeys().getUsage(key.id()); // caps with used/remaining/resetsAt + counters
atlas.apiKeys().update(key.id(), new UpdateApiKeyBody().clearLimits());
```

Periods are `request` (caps a single debit), `day` and `month` (UTC calendar
windows) and `total` (never resets). Reusing an idempotency key replays the first
outcome (`idempotentReplay()`) without counting again; reusing it with a different
unit or amount throws `ConflictException` with `IDEMPOTENCY_CONFLICT`.
`apiKeys().verify(...)` also returns the key's `name`, `scopes`, `constraints`,
`limits` and `usage`. The `api_key.created`, `api_key.updated` and
`api_key.limit_reached` webhooks carry the key's configuration; decode their `data`
as `ApiKeyWebhookData`.

### Session keep-alive

If your app loads Atlas on only some pages, keep the session's inactivity window
moving from your backend:

```java
SessionTouch s = atlas.sessions().touch(sessionId);
s.extended();       // false when a touch within the last minute already moved it
s.idleExpiresAt();  // epoch ms the session ends from inactivity
```

It never extends past the absolute expiry and never revives an ended session
(`ConflictException` with `SESSION_REVOKED`, `SESSION_IDLE_EXPIRED` or
`SESSION_EXPIRED`). Calling it on every request is safe.

`CreateUserBody.notify(false)` and `ImportExportImportUsersBody.notify(false)` (the
default) guarantee the user is sent no email, SMS or push as a consequence of the
create.

## Back-channel logout

When a user signs out, Atlas POSTs a signed `logout_token` to your app's
`backchannel_logout_uri`. `LogoutTokenVerifier` checks it against the instance
JWKS (signature, `typ`, issuer, audience, `iat`/`exp`, `jti`, the logout event,
`sid`/`sub`, and no `nonce`):

```java
LogoutTokenVerifier logout = LogoutTokenVerifier.builder()
        .jwksUrl("https://api.atlasauth.net/v1/jwks")
        .issuer("https://your-issuer.example")
        .clientId("your_client_id")
        .build();

// Framework-agnostic handler: form body in, status code out.
int handleBackchannelLogout(String formBody, Map<String, String> responseHeaders) {
    responseHeaders.put("Cache-Control", "no-store");
    try {
        LogoutTokenClaims claims = logout.verifyFormBody(formBody);
        if (!seenJtis.add(claims.jti())) {
            return 400;                                   // replayed token
        }
        if (claims.sid() != null) {
            sessions.endBySessionId(claims.sid());        // your session store
        } else {
            sessions.endAllForUser(claims.sub());
        }
        return 200;
    } catch (LogoutTokenException e) {
        return 400;                                       // e.reason() says why
    }
}
```

The verifier does not remember tokens. To reject replays, keep each `jti` for a
few minutes (longer than the token lifetime) in a shared store such as Redis or
your database, and refuse one you have already seen.

## Verifying session tokens

`SessionVerifier` validates an Atlas session JWT locally against cached JWKS —
no call to Atlas on the request path. Build it once and reuse it.

```java
import net.atlasauth.atlas.verify.*;

SessionVerifier verifier = SessionVerifier.builder()
        .jwksUrl("https://api.atlasauth.net/v1/jwks")
        .issuer("https://clerk.your-domain.com")   // required — pins the instance
        .build();

VerifyResult result = verifier.verify(sessionToken);
if (result.ok()) {
    SessionClaims claims = result.claims();
    String userId = claims.sub();

    // Authorization, evaluated straight off the token (no network call):
    result.protect(new ProtectCondition().permission("org:reports:read"));
}
```

It reads whatever a request carries (a `Bearer` header wins over a `__session`
cookie):

```java
VerifyResult result = verifier.authenticateRequest(request.getHeaders());
```

`verify()` is local and pins RS256, the issuer, `exp`/`nbf` (±5s skew), and
guards against an OP access/id token being replayed as a session. For the rare
operation where the ~60s token lifetime is too long a revocation window, the
documented slow path `verifyOnline()` asks Atlas directly (configure `secretKey`
and `bapiBaseUrl` on the builder).

## Error handling

Any non-2xx response throws an `AtlasApiException`. The transport picks the most
specific subclass for the status, so you can catch broadly or narrowly:

```java
import net.atlasauth.atlas.*;

try {
    atlas.users().get("usr_missing");
} catch (NotFoundException e) {
    // 404
} catch (RateLimitException e) {
    // 429
} catch (AtlasApiException e) {
    // anything else: e.status(), e.code(), e.errors(), e.hasCode("...")
}
```

Subclasses: `InvalidRequestException` (400/422), `AuthenticationException`
(401), `PermissionException` (403), `NotFoundException` (404),
`ConflictException` (409), `RateLimitException` (429), `ServerException` (5xx).
Connectivity failures throw `AtlasConnectionException`.

`ErrorCodes` names the stable codes you are most likely to branch on, for example
`ErrorCodes.UNKNOWN_FIELD`, `EMAIL_DELIVERY_NOT_CONFIGURED`,
`EMAIL_PROVIDER_REQUIRED`, `EMAIL_PROVIDER_IN_USE`, `PLAN_IN_USE`, `WRONG_KEY_SURFACE` and `INVALID_KEY`:

```java
if (e.hasCode(ErrorCodes.EMAIL_DELIVERY_NOT_CONFIGURED)) { ... }
```

## Spring Boot

Nothing Spring-specific is needed — expose the two long-lived objects as beans
and inject them anywhere:

```java
import net.atlasauth.atlas.AtlasClient;
import net.atlasauth.atlas.verify.SessionVerifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AtlasConfig {

    @Bean
    public AtlasClient atlasClient(@Value("${atlas.secret-key}") String secretKey) {
        return AtlasClient.builder()
                .secretKey(secretKey)
                .build();
    }

    @Bean
    public SessionVerifier atlasSessionVerifier(
            @Value("${atlas.jwks-url}") String jwksUrl,
            @Value("${atlas.issuer}") String issuer) {
        return SessionVerifier.builder()
                .jwksUrl(jwksUrl)
                .issuer(issuer)
                .build();
    }
}
```

Both are thread-safe singletons; inject `AtlasClient` into services and
`SessionVerifier` into a filter/interceptor that authenticates incoming requests.

## License

[MIT](./LICENSE)
