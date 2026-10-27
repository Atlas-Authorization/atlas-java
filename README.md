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
  <version>1.7.0</version>
</dependency>
```

Gradle:

```kotlin
implementation("net.atlasauth:atlas-java:1.7.0")
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

User user = atlas.users().get("user_123");

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

atlas.withStrict(true).users().update("user_123", body);   // just this call
atlas.withStrict(false).users().update("user_123", body);  // opt out when the instance defaults to strict
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

### Domain moves, logout fan-out and managed email

```java
// Move every URL from the old origin to the new one. dryRun first shows the diff.
OriginMove preview = atlas.instance().moveOrigin(
        new OriginMoveBody().from("https://old.example.com").to("https://example.com").dryRun(true));
preview.changes().forEach(c -> System.out.println(c.object() + " " + c.field() + ": " + c.before() + " -> " + c.after()));

// Send the same logout_token your OIDC apps get to another service.
atlas.logoutNotifyEndpoints().create(new LogoutNotifyEndpointBody().url("https://api.example.com/atlas/logout"));

// Set your domain up on your own Resend or Brevo account, then publish the returned DNS records.
ManagedEmail managed = atlas.messaging().createManagedEmail(new ManagedEmailBody()
        .fromAddress("Acme <no-reply@example.com>").transport("resend").apiKey(System.getenv("RESEND_API_KEY")));

// A development instance can send sign-in emails through your provider without a verified domain.
atlas.messaging().enableDevelopmentEmail(new DevelopmentEmailBody().providerId("mp_123"));
atlas.messaging().disableDevelopmentEmail();
```

### OAuth client secret rotation

The previous secret keeps working for `overlapSeconds` (default 24 hours, at most 7 days), so you can deploy the
new one without downtime. Pass `0` to revoke it now, for example after a leak:

```java
OAuthClientWithSecret rotated = atlas.oauthClients().rotateSecret(clientId,
        new RotateOAuthClientSecretBody().overlapSeconds(3600), null);
rotated.clientSecret();             // shown once
rotated.previousSecretExpiresAt();  // epoch ms, or null when the old secret already stopped
```

### Webhooks

`WebhookVerifier` accepts an `atlas-signature` header that carries several signatures separated by spaces
(`v1,<sig> v1,<sig>`). While an endpoint secret rotates, Atlas may sign each delivery with the old and the new
secret; the delivery verifies when any one signature matches.

A rotation stops the old secret at once by default. Pass an overlap (at most 7 days) to keep it signing each
delivery next to the new one while you deploy; choose an overlap only once every receiver accepts several
signatures:

```java
WebhookSecret rotated = atlas.webhooks().endpoints().rotateSecret(endpointId,
        new RotateWebhookSecretBody().overlapSeconds(3600));
rotated.previousSecretExpiresAt();  // epoch ms, or null when the old secret already stopped
```

### Test and review accounts (1.18)

Real Sign in with Atlas accounts confined to one app, for app-store reviewers, CI and demos. Needs a platform key with the
scope `platform:atlas_id_apps:provision` (or `atlas_id_apps:<app id>:provision` for a single app).

```java
var accounts = atlas.platform().atlasIdApps().provisionedAccounts();
AtlasIdProvisionedAccount reviewer = accounts.create(app.id(), new CreateAtlasIdProvisionedAccountBody()
        .email("review@example.com").label("app-store reviewer"));
reviewer.password();                                    // shown once; pass a secret recipient to get sealedPassword instead
accounts.rotatePassword(app.id(), reviewer.id());
accounts.remove(app.id(), reviewer.id());               // ends its sessions and grants

// Test sign-in mode needs a DNS-verified domain
AccountDomain d = atlas.platform().domains().claim("example.com");   // publish d.verification(), then:
atlas.platform().domains().check(d.id());
atlas.platform().atlasIdApps().update(app.id(), new UpdateAtlasIdAppBody().testMode(true).provisionedVerifiedDomainsOnly(true));
```

Errors: `TEST_ACCOUNT_RESTRICTED` (403), `PROVISIONED_ACCOUNT_LIMIT_REACHED` (409), `DOMAIN_NOT_VERIFIED` (422).

### Integrator platform (1.15)

Scoped credentials, the privacy executor, identity integrations, dry runs and trace context. Atlas keys come in
two formats (`atlas_<live|test>_<kind>_...` and the legacy `sk_` / `pk_` / `psk_` / `whsec_`); `KeyFormat` accepts both.

```java
// A secret key for one service: only the scopes it needs, an owner, an expiry
SecretKey key = atlas.secretKeys().create(new CreateSecretKeyBody()
        .name("ci").scopes(List.of("read_only")).owner("platform-team").expiresAtIso("2027-01-01T00:00:00Z"));
key.secret();                                           // shown once
atlas.secretKeys().rotate(key.id(), new RotateSecretKeyBody().overlapSeconds(3600));
Whoami me = atlas.whoami();                             // what this key may do: me.key().effectiveScopes()

// Idempotent provisioning: a repeat create with the same external_ref returns the object (no secret)
atlas.platform().apps().create(new CreatePlatformAppBody().name("Acme").externalRef("acme-prod")
        .metadata(Map.of("product", "acme", "sandbox", true)));
atlas.platform().apps().sandboxTeardown(null, true);    // dry run of "delete every sandbox-tagged app"

// Validate a write without applying it
DryRunResult plan = atlas.dryRun("PUT", "/v1/instance/integrations/acme", Map.of("enabled", true));
atlas.withDryRun().instance().setIntegration("acme", new SetIntegrationBody().enabled(true).name("Acme"));

// W3C trace context reaches the deliveries and executor calls the write triggers
AtlasClient traced = atlas.withTraceparent(Traceparent.create());
```

**Identity integrations.** `instance().setIntegration(id, body)` connects a preset (`chatly`, `forumulate`,
`cookiemunch`, `referalli`) or your own service; `sessions().getIntegrationToken(sessionId, "chatly")` mints its
short-lived token (the server-side `getToken({ for })`).

**Webhooks.** Upsert on every deploy, test inline, replay what a down endpoint missed, and verify v2 signatures
(they cover the URL, so a captured delivery cannot be replayed to another one):

```java
WebhookEndpointWithSecret ep = atlas.webhooks().endpoints().upsertByUrl(
        new UpsertWebhookEndpointBody("https://app.example.com/hooks/atlas")
                .signatureVersions(List.of("v1", "v2")).eventNaming("canonical"));
WebhookTestResult test = atlas.webhooks().endpoints().testNow(ep.id(), "5s");   // status, latency, signature header sent
atlas.webhooks().endpoints().replay(ep.id(), new WebhookWindowBody().since(sinceMs));

WebhookVerification v = new WebhookVerifier(secret)
        .verify(rawBody, headers, "https://app.example.com/hooks/atlas", true);   // requireV2
```

Event names: `WebhookEvents.canonical("organizationMembership.created")` and `WebhookEvents.spellings(...)`.

**Privacy executor.** A DSAR orchestrator calls `privacy()` with a key that has the `privacy:execute` scope
(`lookup`, `createRequest`, `getRequest`, `cancelRequest`); Atlas calls your orchestrator when an account deletion
is scheduled (`privacy().orchestrator().set(...)`). Verify what Atlas sends with `PrivacyCallVerifier`.

**Operator CLI.** `OperatorLogin` runs the device flow and the one-hour token exchange without a stored key:

```java
OperatorLogin login = new OperatorLogin();
OperatorLogin.DeviceAuthorization device = login.start();
System.out.println("Open " + device.verificationUriComplete() + " and confirm " + device.userCode());
OperatorLogin.TokenResponse operator = login.poll(device);
OperatorLogin.TokenResponse scoped = login.exchange(operator.accessToken(), "ins_123", List.of("users:read"));
AtlasClient cli = OperatorLogin.clientFromAccessToken(scoped.accessToken(), null);
```

A root action without a recent second factor throws `OAuthFlowException` with code `STEP_UP_REQUIRED`.

**Errors.** `INSUFFICIENT_SCOPE` (the former `SCOPE_MISSING` is still matched: `e.isInsufficientScope()`,
`e.requiredScope()`), `STEP_UP_REQUIRED`, and `ROUTE_NOT_FOUND` with `e.didYouMean()`.

**Back-channel logout receiver.** `BackchannelLogoutHandler` wraps `LogoutTokenVerifier`: it answers 200 / 400 /
503 + `Retry-After` the way Atlas's retries expect, from any framework:

```java
BackchannelLogoutHandler handler = BackchannelLogoutHandler.builder()
        .verifier(logout)
        .onLogout(claims -> sessions.endFor(claims.sid(), claims.sub()))
        .build();
BackchannelLogoutHandler.Response r = handler.handle(request.getMethod(), body);   // write r.status(), r.body(), r.headers()
```

Also new: `status()`, `delivery()` (health, egress IPs), `auditExports()`, `notices()`, `securityAdvisories()`,
`subProcessors()`, `entitlements()`, `partnerLinks()`, `partnerPlatforms()`, `atlasIdGrants()`, `instance().unknownFieldsReport` /
`openapiDiff` / `integrationHealth` / `embed`. Sealed (`X-Secret-Recipient`) secrets are not supported in this SDK yet.

### Trusted reverse proxy

If a reverse proxy serves the Frontend API from your own origin, give it a proxy secret. It sends the secret in
`X-Atlas-Proxy-Secret` and the browser's IP in `X-Atlas-Client-IP`; Atlas then uses that IP for rate limits,
lockout, risk signals and audit logs.

```java
ProxySecretCreated created = atlas.instanceSecurity().createProxySecret();
created.secret();                                   // shown once; store it in your proxy's environment
atlas.instanceSecurity().getProxySecret();          // set, createdAt, previousExpiresAt (never the secret)
atlas.instanceSecurity().createProxySecret(new CreateProxySecretBody().overlapSeconds(3600)); // rotate
atlas.instanceSecurity().deleteProxySecret();
```

Platform rotations take an overlap too: `platform().apps().rotateInstanceSecret(appId, instanceId,
new RotatePlatformSecretBody().overlapSeconds(0), null)` (default 72 hours), and
`platform().atlasIdApps().rotateSecret(id, new RotateAtlasIdAppSecretBody().overlapSeconds(3600))` (default
24 hours) / `rotateWebhookSecret(id, body)` (default 0).

With a platform key, `platform().atlasIdApps().findUsers(id, email)` answers a privacy request for one app,
`revokeGrant(id, grantId)` revokes one person's approval, and `platform().atlasIdPublisher()` claims and verifies
the publisher name shown on consent screens.

### Policy acceptance

Terms of service, a privacy policy or any policy your users accept at sign-up (and again at sign-in when a new
version takes effect) are managed with `atlas.policies()`:

```java
atlas.policies().create(new CreatePolicyBody().key("tos").title("Terms of service").display("scroll")
        .required(true).appliesTo("sign_in_and_sign_up").reaccept("grace").graceDays(14));
atlas.policies().createVersion("tos", new CreatePolicyVersionBody().version("2026-10-24")
        .contentMarkdown("# Terms ...").summaryOfChanges("New fees section"));
atlas.policies().publishVersion("tos", "2026-10-24", System.currentTimeMillis() + 7 * 86_400_000L);
UserPolicies standing = atlas.policies().forUser("user_123");
```

`listAcceptances` / `exportAcceptancesCsv` list who accepted which version, with the reading evidence.
`atlas.policies().updateSettings(true)` puts the `pol` claim (accepted versions of required policies) on session
tokens; read it with `SessionClaims.pol()` / `hasAcceptedPolicy(key, version)`.

To serve policy content from your own system, connect it with `atlas.policySource()` (`update` takes the endpoint,
sync mode and a write-only signing secret; `test` and `sync` try it) and use `PolicySourceProtocol`:
`verifyFeedRequest(method, pathAndQuery, name -> request.getHeader(name), secret)` checks Atlas's signed feed
request, and `signPublished(key, version, secret)` / `signRetired(key, secret)` produce the signed event body and
headers to POST to the source's `webhookUrl`.

### Referral attribution

With referral attribution on, Atlas reports every interactive sign-up to your referral platform before
`user.created` is sent. `User` gains `referral()` (where attribution stands and, once attributed, who referred them;
null when never reported), `referralCode()` and `referralLink()`. `referral().status()` is `pending`, `attributed`,
`none`, `failed`, `held` or `flagged`; the platform's own status is `referral().referralStatus()`.

```java
atlas.referrals().update(new UpdateReferralSettingsBody()
        .enabled(true)
        .provider("webhook") // the Atlas referral protocol; or "referalli" (the old "referally" is still accepted) or another built-in adapter
        .endpointUrl("https://referrals.example.com/atlas")
        .signingSecret(System.getenv("REFERRAL_SIGNING_SECRET"))
        .security(new UpdateReferralSecurityBody().onHighRisk("hold"))
        .clear("link_template")); // sent as null
ReferralConnectionTest test = atlas.referrals().test(); // ok(), httpStatus(), latencyMs(), error()
ListPage<ReferralCall> failed = atlas.referrals().listCalls(20, "failed", null, null);
atlas.referrals().retryCall(failed.data().get(0).id());

atlas.users().update("user_123", new UpdateUserBody().referralCode("ada-1")); // 409 REFERRAL_CODE_TAKEN if held
atlas.users().flagReferral("user_123", "self-referral", null);

atlas.referrals().simulate("ACME10");                          // a made-up user; nothing is stored
atlas.referrals().retryFailed(null, "signature_rejected");     // after pasting the new secret
atlas.referrals().listCalls(1000, null, null, null, "timeout", "rfc_123"); // error code, starting_after
ReferralBackfillJob job = atlas.referrals().backfill(new ReferralBackfillBody()
        .since("2026-01-01").codeMetadataPath("public_metadata.referral_code"));
atlas.referrals().getBackfill(job.id());                       // running, then completed
atlas.referrals().getForUser("user_123");                      // the stored result, no call to the platform
atlas.users().setExternalId("user_123", "cust_42");            // forwarded to the platform as an alias
```

Enabling without what the provider needs is `422 REFERRALS_NOT_CONFIGURED`. When the platform has not answered by the
time `user.created` is sent, `referral().status()` is `pending` and the `user.referral_resolved` webhook follows; its
`data` is the full user (decode it as `User`), with `attributed`, `none` or `failed`. `user.referral_attributed`
(`UserReferralAttributedEvent`) still fires for a late attribution.

Two-way sync: a provider with the `inbound` capability sends referral changes made after sign-up back to Atlas. The
settings report `inbound()` (`url()` to give the platform, `secretSet()`, `endpointId()`, and `lastEvent()` with
`at()`, `type()`, `outcome()`). Store the platform's event secret with `inboundSecret(...)` (write-only, 16-512
characters) and its subscription id with `inboundEndpointId(...)`; `clear("inbound_secret")` sends null.

Browser capture: `browserSources()` lists the referral programs whose script or cookie the browser reads
(`clear("browser_sources")` goes back to the provider's default, reported by `browserSourcesDefault()`), and
`consentProvider()` picks the consent manager that decides (`auto`, one manager, or `app`).

To give each user your own id before the first referral call, turn on the `before_user_create` hook. Atlas then sends
your endpoint a signed `POST` (verify it with `WebhookVerifier`) of type `user.before_create` for every self-service
sign-up, and stamps the `external_id` and metadata you answer:

```java
atlas.userHooks().updateBeforeUserCreate(new UpdateBeforeUserCreateHookBody()
        .enabled(true)
        .url("https://app.example.com/hooks/before-create")
        .signingSecret(System.getenv("BEFORE_CREATE_SECRET")));
BeforeUserCreateHookTest tested = atlas.userHooks().testBeforeUserCreate(); // ok(), answer(), error()
```

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
    atlas.users().get("user_missing");
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
