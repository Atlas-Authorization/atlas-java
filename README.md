# Atlas Java SDK

The official Java backend SDK for [Atlas](https://atlasauth.net) — a typed client
for the Backend API plus local, network-free session-token verification.

It is the Java peer of `@atlas/backend`: the same secret-key management surface
(`AtlasClient`) and the same `§7.3` session verifier (`SessionVerifier`), built on
nothing but the JDK `HttpClient`, Jackson, and Nimbus JOSE — no framework
required, so it runs the same in a plain `main`, a servlet container, or Spring
Boot.

- **Java 17+**
- Management client covering all 45 Backend API resource namespaces
- Local JWT verification against cached JWKS — no Atlas round-trip on your hot path
- Typed, catchable errors (`AtlasApiException` and status-specific subclasses)

## Install

Maven:

```xml
<dependency>
  <groupId>net.atlasauth</groupId>
  <artifactId>atlas-java</artifactId>
  <version>1.0.0</version>
</dependency>
```

Gradle:

```kotlin
implementation("net.atlasauth:atlas-java:1.0.0")
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
`atlas.sessions()`, `atlas.oauthClients()`, `atlas.webhooks()`, `atlas.saml..`,
`atlas.instanceSecurity()`, and so on for all 45.

### Configuration

```java
AtlasClient atlas = AtlasClient.builder()
        .secretKey(secret)
        .apiUrl("https://api.atlas.dev")        // default; override per instance
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

## Verifying session tokens

`SessionVerifier` validates an Atlas session JWT locally against cached JWKS —
no call to Atlas on the request path. Build it once and reuse it.

```java
import net.atlasauth.atlas.verify.*;

SessionVerifier verifier = SessionVerifier.builder()
        .jwksUrl("https://api.atlas.dev/v1/jwks")
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
