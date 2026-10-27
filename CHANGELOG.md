# Changelog

## Unreleased

### Added

- **Backend API parity.** 452 further operations, each with typed request and response models (`Map` bodies for free-form shapes), an optional-parameter overload, and an `Idempotency-Key` overload on every POST. New resources on `AtlasClient`: `appCatalog`, `ldapServerClients`, `journeys`, `groups` (with `rules`), `migrations` (with `imports`), `managedGrants`, `leastPrivilege`, `deliveryHealth`, `flags`, `mfaFactors` (with `yubikeys` and `smartCardMappings`), `marketing`, `auditEventSchemas`, `directoryPullSources`, `relayAgents`, `oauthClientProfiles`, `passkeyApi`, `vc` (credential types, credentials, presentation requests), `listen`, `agentRegistration`, `agents`, `agentAccessRequests`, `workflows`, `pageTemplates`, `serviceConnectors`, `desktopMfa`, `identityVerification`, `iga` (entitlements, requests, grants, delegations, SoD, lifecycle, campaigns), `scimProvisioningPresets`, `vault` (with `keyProvider`), `connections` (with `oauthClients` and `webhooks`), `ssf` (streams, receivers), `networkZones`, `accessPolicies`, `abac` (policies, templates, links, schema, evaluation, AuthZEN), `resources` (types, assignments, checks), `orgRoleGrants`, `devices`, `authzDecisions`, `policyBundles`, `proxyApps` and `mcpGateways` (with `budgets`).
- **Added to existing resources.** `users` (required actions, notes, history, minor status, guardians, search and saved views, links, device keys), `organizations` (webhook endpoints, connected apps, connection secret), `oauthClients` (app settings, trusted issuers, workload identity, consent and login requests, exchange policy, profile, delegated UI), `orgLdapConnections` (sync, relay, sync runs), `analytics` (funnels, reports, alert rules, API usage), `instance().integrations()`, `actions().calls`, `fga` and `openFga` (`listUsers`, `readChanges`), `attackProtection` and `riskBasedMfa` secrets, and `integrations().runCardAction`.
- `Transport.getBytes` returns a raw response body; `policyBundles().archive` uses it, and `listen().streamSession` returns the event stream as text.
- Compatibility: no existing public method, overload or constructor changed. `delivery().health()` and `health(kind, state)` are untouched; the single-target read is the new `deliveryHealth().get(kind, id)` (class `DeliveryHealthTargets`, named so no new class shares a simple name with an existing one). `Transport` keeps its API; its response handling now reads bytes internally.
- API coverage list: the 452 operations.
- **Risk-event timeline.** `atlas.riskBasedMfa()`: `listEvents` (`GET /v1/risk_events`; `userId`, `level`, `limit`, `before`), `eventMetrics` (`GET /v1/risk_events/metrics`) and `eventFeedback(id, verdict)` (`POST /v1/risk_events/{id}/feedback`, verdict `legit` or `fraud`), with typed `RiskEvent` and `RiskEventMetrics`.
- **Fraud lookup.** `atlas.riskBasedMfa().fraudLookup(FraudLookupBody)` (`POST /v1/fraud/lookup`) with typed `FraudLookup`.
- `RiskBasedMfa` and `UpdateRiskBasedMfaBody` gain `mode`, `levelActions`, `reputation`, `fraud`, `baseline` and `midSession`.
- API coverage list: the four new operations.

## 1.18.0 — 2026-10-25

### Added

- **Test and review accounts.** `atlas.platform().atlasIdApps().provisionedAccounts()` (`ProvisionedAccounts`): `list`, `create`, `get`, `remove` and `rotatePassword` for `/v1/platform/atlas_id_apps/{id}/provisioned_accounts`, with typed `AtlasIdProvisionedAccount`, `AtlasIdProvisionedAccountList`, `CreateAtlasIdProvisionedAccountBody` and `RotateAtlasIdProvisionedPasswordBody`. The password comes back exactly once, on create and on rotation; pass a secret recipient (`create(..., idempotencyKey, secretRecipient)`, `rotatePassword(..., body, secretRecipient)`) to receive it sealed as `sealedPassword`. The password never appears in the models' or bodies' `toString()`.
- **Account domains.** `atlas.platform().domains()`: `list`, `claim`, `check` (alias `verify`) and `remove` for `/v1/platform/domains`, with typed `AccountDomain` and `AccountDomainList`. Verified domains gate test sign-in mode.
- **Test sign-in mode.** `AtlasIdApp` and `AtlasIdAppWithSecret` gain `testMode` and `provisionedVerifiedDomainsOnly`; `UpdateAtlasIdAppBody` gains `testMode(...)` and `provisionedVerifiedDomainsOnly(...)`.
- Scopes: the provisioning routes need `platform:atlas_id_apps:provision`, or `atlas_id_apps:<app id>:provision` for a single app.
- Error codes `ErrorCodes.TEST_ACCOUNT_RESTRICTED` (403), `ErrorCodes.PROVISIONED_ACCOUNT_LIMIT_REACHED` (409) and `ErrorCodes.DOMAIN_NOT_VERIFIED` (422, also returned when an expired test account signs in).
- API coverage list: the provisioned-account and domain operations.

## 1.17.0 — 2026-10-25

### Added

- **Partner platforms.** `atlas.partnerPlatforms()` (`PartnerPlatforms`): `list`, `get`, `set`, `remove`, `registration`, `storeToken` and `deliveries` for `/v1/partner_platforms`, with typed `PartnerPlatform`, `PartnerPlatformList`, `PartnerPlatformRegistration`, `PartnerDelivery`, `SetPartnerPlatformBody` and `StorePartnerPlatformTokenBody`. The partner token is write-only: it is stored encrypted, never appears in a response model, and the body class does not print it.
- **Per-call audience.** `SetIntegrationBody.perCallAudience(...)` (`param`, `template`, `allowed`, `any_matching`; `clear("per_call_audience")` turns it off) and `sessions().getIntegrationToken(id, integration, audience, audienceParams)` to mint a token for one audience (a site id, a channel, an organization id).
- **Privacy orchestrator.** `SetPrivacyOrchestratorBody` gains `siteId`, `partnerLinkId` and `cancelDeletionOnSignIn`. An outbound partner link's `source` can be `registration`.
- `UserSearchParams.includeDeleted` (`GET /v1/users`), and `UserEmailMatch.deleted`.
- `OperatorConfiguration` gains `audiences` and `accessTokenClaims`.
- Error code `ErrorCodes.MANAGED_WAF_NOT_CONFIGURED` (422, `meta.action`).
- The Sign in with Atlas grant routes under `/v1/platform/atlas_id_apps/{id}` also accept an instance secret key (documented on `platform()` and `atlasIdGrants()`).
- API coverage list: the partner platform operations.

## 1.16.0 — 2026-10-24

### Added

- **Enterprise SSO claims.** `SessionClaims` gains `ssoConnectionId()`, `ssoOrganizationId()` (the Atlas organization that owns the connection, null for an instance-wide one, independent of `orgId()`) and `ssoConnectionType()` (`saml` | `oidc`). New `OidcClaims` for relying parties: `organizationOf`, `organizationsOf`, `ssoOf` (`SsoContext`: `connectionId`, `organizationId`, `type`, `tenantId`, `issuer`, `domain`) and `authorizeUrlWithOrganization`. Scope a per-organization "require SSO" rule on `organizationId`, never on "signed in via SSO".
- **Adopting a Sign in with Atlas app.** `platform().atlasIdApps().setManagedBy(id, instanceIdOrNull)` (`PUT /v1/platform/atlas_id_apps/{id}/managed_by`, `AtlasIdAppManagedBy`). `AtlasIdApp` and `AtlasIdAppWithSecret` gain `managedVia` (`instance_toggle` | `adopted`) and `buttonRenderedOrigins` (`AtlasIdOriginSeen`).
- **Instance-surface app setup check.** `atlasIdGrants().preflight(appId)` (`GET /v1/atlas_id_apps/{id}/preflight`). `AtlasIdAppPreflight` gains `buttonRenderedOrigins`, `authorizeRequests7d` (`AtlasIdAuthorizeSources`), `fallbackOrigins` and `homepageIncludesScript`.
- **Dry run.** `DryRunResult` gains `warnings`, `wouldReturnStatus`, `suppressed` and `validationSkipped` (`DryRunValidationSkipped`); `mode` is `native`, `rollback` or `plan`.
- **Sealed secrets.** `AtlasClient.builder().secretRecipient(...)` and `withSecretRecipient(...)` send `X-Secret-Recipient` on every call, so every route that returns a shown-once secret answers sealed. `sealedSecret`, `sealedClientSecret`, `sealedWebhookSecret` and `secretsSealed` (`SecretsSealed`) are on every secret-bearing response record (webhook endpoints and secrets, OAuth clients, secret keys, Sign in with Atlas apps, SCIM tokens, machines, API keys, platform apps and keys, proxy secrets, partner links).
- `WebhookEndpoint.lastFailingSince`.
- API coverage list: two more operations.

### Compatibility

Records that gained components keep a constructor with the previous component list.

## 1.15.0 — 2026-10-24

### Added

- **Credentials.** `secretKeys()`: `create` (`CreateSecretKeyBody`: scopes or the `read_only` / `privacy:execute` / `privacy:grants:revoke` presets, `owner`, `expiresAt` or `expiresAtIso`, `allowedIps`, `keyFormat`), `list`, `get`, `update` (`UpdateSecretKeyBody`, `clear(...)` sends `name` / `owner` / `expires_at` as null), `rotate` (`RotateSecretKeyBody`: `overlapSeconds`, default 72 h, 0 = now) and `revoke`; the typed `SecretKey` carries the one-time `secret`. `keys().list()` (`GET /v1/keys`, `StandardKey`) and `whoami()` (`Whoami`: the key, its effective scopes, the instance and its issuer / JWKS), also `platform().whoami()`. `platform().keys()` (list with `kind` / `status` / `instanceId`, create, update, revoke) and `platform().audit()` (account audit log, `streams()` CRUD and `test`). `partnerLinks()` (create, list, get, `rotateSecret`, `revoke`; `outbound()` list / save / test / remove) and `atlasIdGrants()` (`listApps`, `lookup(appId, email)`, `revoke(appId, grantId)`).
- **Key formats.** `KeyFormat.parse` / `isSecretKey` / `isPlatformKey` / `isPublishableKey` / `isWebhookSecret` / `isPartnerLinkSecret` / `isAccessToken` accept the suite format `atlas_<live|test>_<kind>_...` next to `sk_` / `pk_` / `psk_` / `whsec_` / `dsk_`; the client takes either as `secretKey` / `platformKey`.
- **Operator sign-in and token exchange.** `net.atlasauth.atlas.operator.OperatorLogin`: the RFC 8628 device flow against the public atlas-cli client (`getConfiguration`, `start`, `poll` with `slow_down`), the RFC 8693 `exchange` for a one-hour token bound to an `ins_` / `app_` / `acct_` resource, `partnerLinkToken` (client_credentials), `revokeLeaked` (`POST /v1/keys/revoke_leaked`) and `clientFromAccessToken`. A failed step throws `OAuthFlowException` (`STEP_UP_REQUIRED` for a root action without a recent second factor).
- **Privacy executor.** `privacy()`: `discover`, `preset`, `lookup` (`PrivacyLookupBody`), `createRequest` (`CreatePrivacyRequestBody`, with an `Idempotency-Key`), `getRequest`, `cancelRequest`, and `orchestrator()` get / set / delete (`SetPrivacyOrchestratorBody`). `PrivacyCallVerifier` verifies the signed case calls and `privacy_request.*` events Atlas sends (`PrivacyCall`: delivery id, timestamp, `traceparent`, body, event).
- **Identity integrations.** `instance()`: `listIntegrations`, `getIntegration`, `setIntegration` (`SetIntegrationBody`), `deleteIntegration`, `integrationDeliveries` (`trace`, `kind`), `revealIntegrationEventsSecret`, `integrationAtlasIdToken`; `sessions().getIntegrationToken(id, integration)` (the server-side `getToken({ for })`, `IntegrationToken`); `integrations()`: `reportVerifyError`, `card`, `revokeSessions`, `instanceMetadata`.
- **Webhooks.** `endpoints().get`, `listWithEvents(includeDeprecated, metadata)`, `upsertByUrl` (`UpsertWebhookEndpointBody`), `testNow(id, wait)` (`WebhookTestResult`), `redeliverWindow` and `replay` (`WebhookWindowBody`, `WebhookBulkResult`), `webhooks().deliveries(id, limit, startingAfter, status, eventType, since, until)` (cursor page with `latencyMs`). Create / update / upsert bodies take `eventNaming`, `external`, `signatureVersions`, `alertContacts`, `description`, `destination` (aws_eventbridge, gcp_pubsub, cloudflare_queue) with `destinationCredentials`, `externalRef`, `metadata`. `WebhookEndpoint` gains the health block (`health`, `lastSuccessAt`, `lastFailure`, `consecutiveFailures`, `successRate24h`, `attempts24h`, `alertsSent`, `autoDisable`, `replay`). `delivery()`: `egressIps`, `health(kind, state)`, `setAlertContacts`. `WebhookVerifier.verify(body, headers, endpointUrl, requireV2)` checks v2 URL-bound signatures (unknown versions ignored). `WebhookEvents`: the canonical / alias table (`canonical`, `isDeprecated`, `spellings`) and the constants for the health, secret-key, privacy-request, deletion and status events.
- **Provisioning.** `externalRef` and `metadata` on `CreatePlatformAppBody`, `UpdatePlatformAppBody`, `CreateOAuthClientBody` (with `requirePkce`), `CreateAtlasIdAppBody`, `UpdateAtlasIdAppBody` and the webhook bodies; `metadata`, `externalRef` and `idempotentReplay` on the matching response records; `ListPlatformAppsParams.metadata` and `Platform.Apps.listSecretKeys` / `AtlasIdApps.list` metadata filters (`metadata[key]=value`); `platform().apps()`: `createSecretKey`, `cloneConfig`, `sandboxTeardown`.
- **Dry run and trace context.** `atlas.withDryRun()` sends every write with `?dry_run=true` and no `Idempotency-Key`; `atlas.dryRun(method, path, body)` returns the plan (`DryRunResult`). `atlas.withTraceparent(...)` (and `Builder.traceparent`) sends a W3C `traceparent`; `Traceparent.create` / `parse` / `fromHeaders`.
- **Status, notices and compliance.** `status()` (service status, subscription, rotate feed, incidents, dependencies), `notices()`, `securityAdvisories()`, `subProcessors()`, `auditExports()` (CloudEvents), `entitlements().get(subjectType, subjectId)`.
- **Instance.** `preflight(minSeverity, includeSuppressed)` (findings gain `group` / `suppressed`; the result gains `minSeverity`, `suppressed`, `integrations`), `unknownFieldsReport`, `openapiDiff`, `integrationHealth` / `integrationHealthProviders`, `embed`, `messaging().channel`, `design().importBrandUrl` / `removeBrand`, `referrals().install`, `ListEventsParams.since` / `until` / `order`, `UserSearchParams.updatedAfter` / `updatedBefore`, `ListConfigChangelogParams.includeNoOp`. Cursor pages (`starting_after` / `next_cursor`): `organizations().memberships().list(orgId, limit, startingAfter)`, `appRoles().listUsersAfter`, `logoutNotifyEndpoints().listDeliveries(id, limit, startingAfter)`, `apiKeys().listRevoked(since, limit, startingAfter)`.
- **Errors.** `ErrorCodes.INSUFFICIENT_SCOPE` (the old `SCOPE_MISSING` is still matched: `AtlasApiException.isInsufficientScope()`, `requiredScope()`), `STEP_UP_REQUIRED` (`isStepUpRequired()`), `ROUTE_NOT_FOUND` (`didYouMean()`; `ErrorItem` gains `docsUrl`, `didYouMean`, `allowedMethods`), `NOT_A_LIVE_INSTANCE`, `TEST_MODE_ONLY`, `TESTING_UNAVAILABLE`, `PARTNER_LINK_UNAVAILABLE`, `ORIGIN_NOT_ALLOWED`.
- **Back-channel logout.** `BackchannelLogoutHandler`: a framework-neutral receiver (`handle(method, formBody)` returns the status, JSON body and headers) that verifies the token, calls your `onLogout`, and answers 200 (also for a replay), 400 (refused, not retried) or 503 with `Retry-After` (keys unreachable or `onLogout` threw).
- Referral provider `referalli` (the old `referally` is still accepted); `AtlasConstants` gains the preflight codes, status components, integration presets, privacy types and statuses and webhook destination types.
- The API coverage list now holds all 720 Backend and Platform API operations.

### Not included

- Sealed secrets (`X-Secret-Recipient`, `sealed_secret`): opening a libsodium sealed box needs XSalsa20-Poly1305, which the JDK does not provide and which this SDK does not hand-roll. It will return with a vetted dependency (lazysodium or BouncyCastle). Until then secrets are returned in plaintext, once, as before.

## 1.14.0 — 2026-10-24

### Added

- **Policy acceptance.** `policies()`: `list` / `create` / `get` / `update` / `delete` (a never-published policy is deleted, one with history is retired), `listVersions` / `createVersion` (a draft, or `publish` with `effectiveAt`) / `getVersion` / `updateVersion` / `deleteVersion` (drafts only) / `publishVersion` (optional `effective_at`, epoch ms or ISO 8601) / `retireVersion`, `listAcceptances` and `exportAcceptancesCsv`, `forUser` (a user's standing on every policy) and `getSettings` / `updateSettings` (the `pol` session claim). Typed `Policy`, `PolicyVersion`, `PolicyAcceptance`, `PolicyStanding`, `PolicySettings`. `policySource()`: `get` / `update` (the signing secret is write-only) / `delete` / `test` / `sync` / `listDeliveries`.
- **Policy source protocol helpers.** `net.atlasauth.atlas.policy.PolicySourceProtocol`: `verifyFeedRequest` checks Atlas's signed `GET {endpoint_url}/policies` (`atlas-signature` `v1,<base64 HMAC-SHA256>` over `{id}.{timestamp}.GET\n{path+query}`, millisecond timestamps, several `v1` values accepted, 5-minute window; the result carries the `key` query parameter), and `signPublished` / `signRetired` / `signEvent` sign `policy.published` / `policy.retired` events for Atlas. Cross-checked against the TypeScript reference vectors.
- `SessionClaims.pol()` and `hasAcceptedPolicy(key, version)` read the `pol` claim. `AtlasConstants.WEBHOOK_EVENT_USER_POLICY_ACCEPTED` and `WEBHOOK_EVENT_POLICY_VERSION_PUBLISHED`.
- The API coverage list now holds all 626 Backend and Platform API operations.

## 1.13.0 — 2026-10-24

### Added

- **Migrate from Better Auth: sealed-bundle imports.** `betterAuthImports()`: `getPublicKey`, `rotatePublicKey`, `create` (`BetterAuthImportOptions`), `list`, `get`, `uploadPart` (a `text/plain` part), `finalize`, `dryRun`, `apply` (resumes a failed import; `confirmConflicts`), `delete`, `listJwks`, `deleteJwk`. Helpers: `upload(bundle[, options, partBytes, onPart])` creates the import, uploads the bundle in line-aligned parts of at most 8 MiB (`BetterAuthImports.splitBundleParts`, `PART_BYTES`) and finalizes it; `waitFor(id[, interval, timeout, onProgress])` polls until the import leaves `analyzing` / `queued` / `applying`. Typed `BetterAuthImport` (with `BetterAuthImportReport`: models, skipped, conflicts, action items), `BetterAuthImportKey`, `BetterAuthImportPart`, `ImportedJwk`.
- **Better Auth session bridge.** `betterAuthBridge()`: `get` / `update` (`UpdateBetterAuthBridgeBody`; the old app's secret is write-only, `LiveFallback.clearSecret()`) / `delete`, and `exchange(token[, clientIp, userAgent, remember])` on the secret-key Backend API route, forwarding the end user's IP and user agent (`X-Atlas-Client-IP`, `X-Atlas-Client-User-Agent`). `net.atlasauth.atlas.betterauth`: `BetterAuth.exchange` classifies the outcome (`ExchangeResult`: `SESSION`, `NEEDS_MFA`, `REFUSED` with the `meta.bridge_outcome`; never throws for a refusal or an outage); `SessionBridge` turns a request's cookies into a `BridgeDecision` (Atlas cookies to set, Better Auth cookies to clear only on a final refusal, `continuationUrl` for a second factor); `BetterAuth.readSessionCookie`, `sessionCookieNames`, `allCookieNames`, `BridgeCookie.toHeader`. Spring: `atlas.better-auth-bridge.enabled=true` (with `atlas.secret-key`; `cookie-prefix`, `cookie-name`, `better-auth-cookie-domain`, `secure`, `sign-in-url`) makes `AtlasAuthenticationFilter` exchange a GET/HEAD's Better Auth cookie and answer 303 to the same URL signed in (or to the sign-in page when a second factor is owed). `Transport.requestWithHeaders` sends per-call headers (never `authorization`).
- **Better Auth JWTs during the cutover.** `SessionVerifier.Builder.legacyIssuers(List<LegacyIssuer>)` (`LegacyIssuer.builder(issuer).until(...).jwks(...) | jwksUrl(...)`, `audience`, `algorithms`, `resolveSubject`): a token from that issuer, inside its window, is verified against that issuer's keys only (EdDSA/Ed25519 via the JDK, ES256, ES512, RS256, PS256; `exp` required) and its `sub` is mapped to the Atlas user id through `external_id` (Backend API lookup with `secretKey` / `bapiBaseUrl`, cached 5 minutes, at most 10 000 entries; failures are not cached). The claims carry `external_id` and `legacy`. Spring: `atlas.legacy-issuers[n].issuer` / `until` / `jwks-url` / `audience` / `algorithms`.
- The API coverage list now holds all 604 Backend and Platform API operations.

## 1.12.0 — 2026-10-24

### Added

- **DPoP (RFC 9449) on the access-token verifier.** A DPoP-bound token (`cnf.jkt`) is accepted only with the DPoP scheme and a valid proof (never as Bearer): `typ` `dpop+jwt`, `Dpop.ALGS` (ES256/384/512, RS256/384/512, PS256/384/512, EdDSA/Ed25519 via the JDK), public embedded JWK, signature, `htm`, normalised `htu` (`Dpop.normalizeHtu`; `Builder.publicOrigin` for path-only URLs), `iat` window, `ath`, `jti` replay via a `DpopReplayStore` (`MemoryDpopReplayStore` by default), RFC 7638 `Dpop.jwkThumbprint` match. `Builder.requireDpop` refuses unbound tokens; `Builder.dpopNonces` (`HmacDpopNonceSource`) demands a server nonce (`use_dpop_nonce`, `Result.dpopNonce()` for the `DPoP-Nonce` header). `verifyRequest(ResourceRequest)`; `Result` gains `dpopBound` / `dpopNonce` (the old constructor stays). Challenges use the `DPoP` scheme with `algs` (`buildWwwAuthenticate(scheme, ...)`). `AtlasAccessTokenFilter` checks the proof against the servlet request and sets `DPoP-Nonce`. Metadata advertises `dpop_signing_alg_values_supported`; `protectedResourceMetadata(..., dpopBoundAccessTokensRequired)`.
- **Billing: several subscriptions per subject, add-ons, Chargebee / Autumn / Commet.** `billing().getSubscription`, `syncPlanEntitlements`, `getSettings` / `updateSettings` (`BillingSettings`, limit merge `max` / `sum`), `checkFeature` (`BillingFeatureCheck`). `BillingPlan` gains `limits`, `kind`, `seatFeatureId`; `BillingSubscription` gains `entitlements` (`BillingEntitlements`); plan bodies take `kind`, `limits`, `seatFeatureId`; `ReportBillingUsageBody` takes `feature` and `subscriptionId`. `AtlasConstants.BILLING_PROVIDERS`.
- **Custom OAuth / OIDC providers.** `customOAuthProviders()`: `list`, `get`, `create` / `update` (`CustomOAuthProviderBody`), `delete`, `discover`, `previewProfileMapping` (ad hoc or by id, `ProfileMappingPreview`), `listSigningKeys` / `rotateSigningKey` / `retireSigningKey` (`UpstreamSigningKey`). SSO connections gain `previewProfileMapping` and the signing-key methods; their bodies take the relying-party settings, and `SsoConnection` reports them plus `upstreamUrls`.
- **Actions gallery.** `actionGallery()` (`list`, `get`, `install`, `installs`, `update`) and `actionTemplates()` (`list`, `create` / `update` with `ActionTemplateBody`, `get`, `delete`, `addVersion`, `submitVersion`, `deleteVersion`, `publish`, `unlist`). `ActionTestResult.httpRequests`; sample events take `user.organizations` and `request.country`.
- **Session custom data.** `sessions().setCustomData` (replace; null clears), `updateCustomData` (merge; a null value removes a key), `create(userId, actorSub, customData)`; `Session.customData`, `MintedSession.customData`. `AtlasConstants.WEBHOOK_EVENT_SESSION_UPDATED`.
- **Sign-in settings.** `instanceSecurity().setWalletRpcSecret`; rate-limit `rules` (`RateLimitRule`) on `UpdateRateLimitPolicyBody` and `RateLimitPolicy` (plus the verify / LDAP budgets and the group catalog); `instance().setAccountLinkingTrust`, `instance().setCaptchaConfig`; `AtlasConstants.CAPTCHA_PROVIDERS` (incl. `captchafox`, `vercel_botid`), `LINKING_TRUST_LEVELS`.
- **Vercel BotID attestations.** `BotId.createAttestation(secret, isBot, isVerifiedBot[, ttlSeconds, nowSeconds])` in Atlas's `atlas_botid.v1` format.
- **Enterprise SSO.** SAML Single Logout and encrypted-assertion fields on SSO bodies and `SsoConnection`; `SsoConnectionSamlMetadata.sloUrl`; `ssoConnections().getSamlEncryptionKey`, `createSamlEncryptionKey`, `retirePreviousSamlEncryptionKey`, `deleteSamlEncryptionKey` (`SamlEncryptionKey`). Attack protection `dormantAccounts` (`DormantAccountsSettings`); risk weight / signal `dormantAccount`; `AtlasConstants.WEBHOOK_EVENT_USER_REACTIVATED`, `EMAIL_TEMPLATE_ACCOUNT_REACTIVATED`.
- **Atlas as OAuth provider.** `OAuthClient.accessTokenFormat` / `tokenLifetimes` (`OAuthTokenLifetimes`) and the same on client bodies; resource servers report and take `accessTokenFormat`, refresh lifetimes and `introspectionClientIds`. `instanceSecurity().getSigningKeyProvider` / `setSigningKeyProvider` / `removeSigningKeyProvider` (`SigningKeyProviderSettings`; AWS KMS, Google Cloud KMS, Azure Key Vault).
- **Opaque access tokens.** `AccessTokenVerifier.Builder.introspection(Introspector)`: non-JWT access tokens are validated at Atlas's RFC 7662 endpoint with your API's client credentials, cached by token hash (TTL, negative TTL, never past `exp`, bounded); `Introspector.Builder.introspectJwts(true)` checks JWTs live too.
- **Device sign-in approval.** `DeviceAuthorizations(frontendApi, sessionToken[, publishableKey, httpClient])`: `lookup`, `approve`, `deny` with the user's session token; `DeviceAuthorizationException` (`needsReverification`, `isInvalidCode`); `formatUserCode`, `userCodeFromUrl`.
- The API coverage list now holds all 588 Backend and Platform API operations.

Records keep their previous constructors.

### Changed

- Without introspection, a token that is not a JWT is refused with "configure introspection to accept opaque tokens" instead of "malformed".

## 1.11.0 — 2026-10-23

### Added

- **App roles.** `appRoles()`: `list`, `create`, `get`, `update`, `delete`, `listUsers`, `getForUser`, `setForUser` (replace), `changeForUser` (add/remove), `removeFromUser`. `SessionClaims.hasAppRole` / `hasAppPermission` / `appRoles()` / `appPermissions()` and `ProtectCondition.appRole` / `appPermission` read the `app_roles` / `app_permissions` claims (no active organization needed).
- **Organization teams and custom roles.** `organizations().teams()` (list, create, get, update, delete, members list/add/remove) and `organizations().roles()` (the roles an org can assign; create/update/delete its own, with `reassignTo`). `organizations().getBySlug`, `checkSlug`, `invitations().resend`.
- **User search.** `users().list(UserSearchParams)`: `query`, `emailAddress`, `username`, `phoneNumber`, `externalId`, `banned`, `emailVerified`, `organizationId`, `createdAfter` / `createdBefore`, `orderBy`, `offset`, `includeTotalCount`.
- **Timed bans.** `users().ban(id, reason, note, expiresInSeconds, expiresAt)`; `User.banExpiresAt`.
- **API key controls.** `CreateApiKeyBody` `prefix`, `expiresInSeconds`, `enabled`, `rateLimit` (`ApiKeyRateLimit`), `remaining`, `scopes`; `UpdateApiKeyBody` `enabled`, `rateLimit`, `remaining`, `scopes` (clear with `set("rate_limit", null)` / `set("remaining", null)`). `apiKeys().verify(secret, requiredScopes)`; `ApiKeyVerification` carries `code` (`insufficient_scope`, `rate_limited`, `usage_exhausted`), `missingScopes`, `retryAfterMs`, `remaining`. `ApiKey` reports `enabled`, `rateLimit`, `remaining`, `scopes`.
- **Billing providers (bring your own key).** `billing()`: `listProviders`, `getProvider`, `setProvider`, `testProvider`, `deleteProvider(force)`, `changeSubscription`, `cancelSubscription(immediately)`, `restoreSubscription`. Plan bodies and `BillingPlan` carry `provider`, `providerPriceId`, `annualPriceId`, `annualAmount`; `BillingSubscription` the provider, account, interval, scheduled change, trial and cancellation fields.
- **OAuth server administration.** `instanceSecurity().listSigningKeys` / `rotateSigningKey(alg, gracePeriodSeconds, emergency)`; `oauthInitialAccessTokens()` (list, create, revoke). OAuth client bodies take `disabled`, `requirePkce`, `subjectType`, `sectorIdentifierUri`, `secretExpiresInSeconds` and `OAuthClient` reports them (with `clientSecretExpiresAt`); resource servers take and report `publishResourceMetadata`.
- **Access-token verification for your API.** `AccessTokenVerifier` checks an Atlas-issued OAuth access token locally (issuer, expiry, `token_use`, audience, scopes) and returns the RFC 6750 / RFC 9728 `WWW-Authenticate` challenge on refusal; `buildWwwAuthenticate`, `protectedResourceMetadata`, `protectedResourceMetadataPath`; the Spring `AtlasAccessTokenFilter`. DPoP-bound tokens are refused.
- `scimTokens().update`, `userHooks().listBeforeUserCreateCalls`, `referrals().getCall`; SCIM token bodies take `scopes` and `roleMappings`.

### Changed

- Session and logout-token verification accept **ES256** as well as RS256 (`SigningAlgs.ALGS`), and nothing else. The records that gained fields keep their previous constructors.

## 1.10.0

### Added

- Referral settings carry browser capture: `browser_sources` (the referral programs whose script or cookie the browser reads; null on update resets to the provider's default), `browser_sources_default`, and `consent_provider` (`auto`, one consent manager, or `app`). The earlier `ReferralSettings` constructors keep working.

## 1.9.0

### Added

- **Two-way referral sync.** Referral settings carry two-way sync: `inbound` (`url`, `secret_set`, `endpoint_id`, `last_event`) on reads, write-only `inbound_secret` and `inbound_endpoint_id` on updates (null clears), and provider capability `inbound`. The earlier `ReferralSettings` and `Capabilities` constructors keep working.

## 1.8.0

### Added

- **Referral attribution v2.** `referrals().retryFailed(kind, errorCode[, idempotencyKey])`
  (`POST /v1/instance/referrals/calls/retry_failed`, `ReferralRetryFailedResult` with `requeued`),
  `referrals().simulate(code)` (`POST /v1/instance/referrals/simulate`, `ReferralSimulateResult`: `callId`,
  `customerRef`, `result`, `response`), `referrals().backfill(ReferralBackfillBody[, idempotencyKey])` and
  `referrals().getBackfill(id)` (`POST /v1/instance/referrals/backfill`, `GET /v1/instance/referrals/backfill/{id}`;
  `ReferralBackfillBody`: `since`, `codes`, `codeMetadataPath`; `ReferralBackfillJob`: `usersScanned`, `usersSkipped`,
  `callsQueued`, `truncated`, `nextSince`, `calls`), and `referrals().getForUser(userId)`
  (`GET /v1/users/{id}/referral`, `UserReferralResult`).
- **Settings.** `ReferralSettings` gains `trackingToken`, `codeLookupUrl`, `signupCodeField`, `forwardLifecycle`,
  `mirrorToPublicMetadata` and `includeReferralClaims`; `UpdateReferralSettingsBody` gains the same setters (`clear`
  takes `tracking_token` and `code_lookup_url`). `provider` can be `referally`. `Capabilities` gains `alias`, `user`
  and `ping`. `Connection` gains `lastPing` (`ReferralPing`) and `alert` (`Alert`: `signature_rejected` or
  `connection_paused`). `ReferralConnectionTest` gains `method` (`ping` or `probe`), `ping` and `warnings`.
- **Call log.** New `listCalls(limit, status, kind, userId, errorCode, startingAfter)`; `limit` goes up to 1000.
  `ReferralCall.kind` adds `alias`, `user` and `ping`; calls gain `latencyMs`, `errorCode`, `response`, `highlights`
  (`Highlights`), `requestId` and `batchId`.
- **Users.** `users().setExternalId(id, externalId)` (`PATCH /v1/users/{id}`; null clears; with referrals on, the
  change is forwarded to the platform as an alias). `UpdateUserBody` gains `externalId` and `clearExternalId()`;
  `User` gains `externalId`. `UserReferral` gains `partnerExternalRef`, `referralStatus` and `notes`. The new
  `user.referral_resolved` webhook carries the full user as `data` (decode it as `User`).
- **before_user_create hook.** New `userHooks()` namespace: `getBeforeUserCreate()`,
  `updateBeforeUserCreate(UpdateBeforeUserCreateHookBody[, idempotencyKey])` and `testBeforeUserCreate()`
  (`/v1/instance/hooks/before_user_create`; `BeforeUserCreateHook`, `BeforeUserCreateHookTest`). The hook call is
  signed like a webhook with type `user.before_create`; verify it with `WebhookVerifier`.
- **Preflight** can report `referrals_secret_invalid`, `referrals_program_inactive`, `referrals_unreachable`,
  `referrals_clock_skew` and `referrals_external_id_unset`.

### Changed

- On the server, `User.referral().status()` is now where attribution stands (`pending`, `attributed`, `none`,
  `failed`, `held`, `flagged`); the platform's own status moved to `referralStatus()`. `user.created` carries
  `status: "pending"` while the platform has not answered.

### Compatibility

- `User`, `UserReferral`, `ReferralSettings` (and its `Connection`, `Capabilities`), `ReferralCall` and
  `ReferralConnectionTest` have more record components; their 1.7 constructors are kept.

## 1.7.0

### Added

- **Referral attribution settings.** New `referrals()` namespace: `get()` and `update(UpdateReferralSettingsBody[,
  idempotencyKey])` (`GET`/`PATCH /v1/instance/referrals`; `ReferralSettings` with nested `Connection`, `LastTest`,
  `Provider`, `Capabilities`; `security` as `ReferralSecuritySettings` on read and `UpdateReferralSecurityBody` on
  write; `clear(key)` sends a nullable field as null), `test()` (`ReferralConnectionTest`), `listCalls()` /
  `listCalls(limit, status, kind, userId)` (`ReferralCall`, `ReferralRisk`) and `retryCall(id[, idempotencyKey])`.
- **Users.** `User` gains `referral` (`UserReferral`: `partnerId`, `referrerExternalId`, `referrerUserId`,
  `matchedVia`, `status`, `riskLevel`, `flagged`), `referralCode` and `referralLink`. `CreateUserBody` gains
  `referral` (`CreateUserReferral`: `code`, `clickId`, `anonId`) and `referralCode`; `UpdateUserBody` gains
  `referralCode` and `clearReferralCode()`. New `users().flagReferral(id[, reason, idempotencyKey])`
  (`UserReferralFlag`).
- **Webhooks.** `UserReferralAttributedEvent` types the `data` of the new `user.referral_attributed` event.
- **Error codes.** `ErrorCodes.REFERRALS_NOT_CONFIGURED` (422) and `ErrorCodes.REFERRAL_CODE_TAKEN` (409). Preflight
  can report `referrals_enabled_unverified` and `referrals_without_bot_protection`.

### Compatibility

- `User` has three more record components; the 13- and 14-argument constructors are kept.

## 1.6.0

### Added

- **Trusted reverse proxy secret.** `instanceSecurity().getProxySecret()` (`ProxySecret`: `set`, `createdAt`,
  `previousExpiresAt`, `secretHeader`, `clientIpHeader`), `createProxySecret()` /
  `createProxySecret(CreateProxySecretBody)` (`overlapSeconds` on a rotation: default 86400, `0` stops the old
  secret now, at most 604800; returns `ProxySecretCreated` with `secret`, shown once, and `rotated`) and
  `deleteProxySecret()`.
- **Webhook secret rotation with an overlap.** `webhooks().endpoints().rotateSecret(id, RotateWebhookSecretBody)`
  (`overlapSeconds`, default 0, at most 604800). `WebhookSecret` gains `warning`, `overlapSeconds` and
  `previousSecretExpiresAt`; `WebhookEndpoint` gains `previousSecretExpiresAt`.
- **Per-instance platform rotation with an overlap.** `platform().apps().rotateInstanceSecret(id, instanceId,
  RotatePlatformSecretBody, idempotencyKey)` (`overlapSeconds`, default 259200, `0` revokes the prior keys now).
  `PlatformInstanceSecret` gains `appId`, `instanceId`, `overlapSeconds` and `previousKeysExpireAt`.
- **Sign in with Atlas apps.** `platform().atlasIdApps().rotateSecret(id, RotateAtlasIdAppSecretBody)` (default
  24 hours) and `rotateWebhookSecret(id, RotateAtlasIdAppSecretBody)` (default 0). `AtlasIdApp` and
  `AtlasIdAppWithSecret` gain `previousSecretExpiresAt`, `secretIssuedAt` and `webhookPreviousSecretExpiresAt`;
  `AtlasIdAppWithSecret` also gains `overlapSeconds`.

### Changed

- `PlatformInstanceSecret.id` is deprecated: the API never sent it; read `instanceId`.
- `platform().apps().rotateInstanceSecret(id, instanceId)` now keeps the prior keys valid for 72 hours (the
  server's new default). Pass `new RotatePlatformSecretBody().overlapSeconds(0)` for the old behaviour.
- Records that gained components keep their 1.5 constructors.

## 1.5.0

### Added

- **OAuth client secret rotation with overlap.** `oauthClients().rotateSecret(id, RotateOAuthClientSecretBody, idempotencyKey)`
  takes `overlapSeconds` (default 86400, `0` revokes the old secret now, at most 604800). `OAuthClientWithSecret`
  gains `previousSecretExpiresAt` and `overlapSeconds`; `OAuthClient` gains `previousSecretExpiresAt`.
- **Development email sender.** `messaging().getDevelopmentEmail()`, `enableDevelopmentEmail(DevelopmentEmailBody)` and
  `disableDevelopmentEmail()` (`DevelopmentEmail`, `DevelopmentEmailDisabled`). `MessagingProviders` gains
  `developmentEmail` (`DevelopmentEmailSummary`).
- **Managed email on your own provider account.** `ManagedEmailBody` gains `transport`, `config` (`apiKey(...)`) and
  `providerId`. `ManagedEmail` gains `backend`, `providerId` and `feedbackWebhook`; `limits` is now null and
  `reputation` is `your_provider_account`. `ManagedEmailDeleted` gains `providerRemoved`.
- **Provider mode.** `MessagingProvider` gains `id` and `mode`; `MessagingProviderInput` gains `mode`; new
  `messaging().setEmail(fromAddress, mode, EmailProviderConfig)`.
- **Allowed origins with an integration.** `allowedOriginEntries(List<AllowedOriginEntry>)` on `UpdateInstanceBody`,
  `CreatePlatformAppBody`, `UpdatePlatformAppBody`, `CreatePlatformInstanceBody` and `CreateAccountlessAppBody`
  (`AllowedOriginEntry.of(origin, REDIRECT | EMBEDDED | AUTO)`). `UpdateInstanceBody` gains `set(key, value)`.
- **Issuer and verification endpoints.** `InstanceConfig` gains `issuer`, `discoveryUrl`, `jwksUri` and
  `allowedOriginIntegrations`; `InstanceUpdateResult` gains `allowedOriginIntegrations`; `PlatformApp`,
  `PlatformAppWithSecret`, `PlatformAppProvisioned` and `AcceptedPlatformApp` gain `frontendApiHost`, `issuer`,
  `discoveryUrl`, `jwksUri` and `allowedOriginIntegrations`.
- **Login readiness.** `LoginReadiness` gains `integration` (`LoginReadinessIntegration`); `LoginReadinessChecks`
  gains `applies`.
- **Email verification.** `EmailAddress` gains `verification` (`EmailAddressVerification`: `status`, `strategy`).
- **Branding audiences.** `BrandingSettings` gains `audiences` and `BrandingResolved` gains `audiences`
  (`BrandingAudience`).
- **Error codes.** `ErrorCodes` adds `READ_ONLY_FIELD`, `DEVELOPMENT_EMAIL_NOT_ALLOWED`,
  `DEVELOPMENT_EMAIL_UNAVAILABLE`, `DEVELOPMENT_EMAIL_LIMIT_REACHED` and `DEVELOPMENT_EMAIL_TEMPLATE_NOT_ALLOWED`.
- **Webhooks, several signatures.** `WebhookVerifier` accepts an `atlas-signature` header with several
  space-separated `v1,<signature>` entries and verifies when any one matches (each compared in constant time).
  A single signature works as before.

### Compatibility

- Every extended record keeps a constructor with its previous components.

## 1.4.0

### Added

- **Back-channel logout, retryable failures.** `LogoutTokenException.retryable()` is true for
  `Reason.NO_KEYS`: the issuer's keys could not be fetched (a network error, a JWKS outage, or a
  just-rotated key whose refetch failed). Answer `503` with `Retry-After` then, so Atlas retries the
  delivery, and `400` otherwise. A token whose `kid` is unknown after a failed JWKS refetch is now
  `NO_KEYS` (was `BAD_SIGNATURE`).
- **Webhook verification.** `new WebhookVerifier(secret).verify(rawBody, headers[, nowMs])` (package
  `net.atlasauth.atlas.webhook`) returns a `WebhookVerification` record (`verified`, `reason`, `event`,
  `deliveryId`, `timestamp`). `deliveryId` is the signed `atlas-id` header, the same on every retry of one
  event: deduplicate on it.
- **Domain moves.** `instance().moveOrigin(new OriginMoveBody().from(..).to(..).dryRun(true))` returns `OriginMove`;
  `platform().atlasIdApps().moveOrigin(id, body)` does it for one app.
- **Connections and integrations.** `instance().connections()` (`InstanceConnections`), and
  `instance().get/set/deleteChatlyIntegration()` and `…ForumulateIntegration()`.
- **Logout fan-out.** New `logoutNotifyEndpoints()` resource: `list`, `create`, `get`, `update`, `delete`,
  `listDeliveries` (`LogoutNotifyEndpoint`, `LogoutNotifyDelivery`).
- **Managed email.** `messaging().getManagedEmail()`, `createManagedEmail(ManagedEmailBody)`,
  `verifyManagedEmail()`, `deleteManagedEmail(force)` (`ManagedEmail`).
- **Back-channel logout logs.** `oauthClients().listBackchannelDeliveries(..)` / `testBackchannelLogout(id)` and the
  same on `platform().atlasIdApps()` (`BackchannelLogoutDelivery`, `BackchannelLogoutTest`).
- **Sign in with Atlas apps.** `platform().atlasIdApps().preflight(id)`, `findUsers(id, email)`,
  `revokeGrant(id, grantId)`, `rotateWebhookSecret(id)`. `AtlasIdApp` / `AtlasIdAppWithSecret` gain
  `webhookUrl`, `webhookEvents`, `webhookActive` (and `webhookSecret`); `UpdateAtlasIdAppBody.webhookUrl`.
- **Publisher.** `platform().atlasIdPublisher()`: `get`, `set`, `setSkipConsent`, `delete`, `verify`; and
  `platform().configChangelog(params)`.
- **More fields.** `User.version`; `CookieConsent.culture` / `declaredCookies`; `CookieConsentBody.culture`;
  `Preflight.sdkVersionsSeen`; `WebhookDelivery.error` / `location`; `WebhookEndpointList.eventCatalog` /
  `eventAudiences`; `CustomDomain.dns` and `domains().create(host, role, dns, idempotencyKey)`;
  `ConfigChangelogEntry.target`; `MessagingProviders.managedEmail`. Each extended record keeps a constructor with
  its previous components.

## 1.3.1

### Added

- **Captcha is bring-your-own-keys.** `ErrorCodes.CAPTCHA_KEYS_REQUIRED` (422) and `ErrorCodes.CAPTCHA_IN_USE` (409, `meta.flows`).
- `instanceSecurity().setCaptchaSecret(secret, provider)` stores the secret for a named provider before captcha is turned on.
- `instanceSecurity().deleteCaptchaSecret(provider, force)` removes a secret a flow uses and turns those flows off (`flows_turned_off`).

## 1.3.0

All changes are backward compatible: new record components have the previous
constructors kept as overloads, and new request fields and parameters are optional.

### Added

- `OAuthClient.skipLogoutConfirmation` and `OAuthClientWithSecret.skipLogoutConfirmation`
  (boolean, default false), plus `CreateOAuthClientBody.skipLogoutConfirmation` and
  `UpdateOAuthClientBody.skipLogoutConfirmation` (optional `Boolean`). First-party clients
  only (a third-party client gets 422 `VALIDATION_FAILED`, param `skip_logout_confirmation`).
  When true, RP-initiated logout (`/oauth2/logout`) with the client's `client_id` and an
  exactly registered `post_logout_redirect_uri` ends the session and redirects straight
  back with `state`, without the confirmation page.
- Cookie-consent settings link: `CookieConsentBody.settingsLink`
  (`CookieConsentSettingsLinkInput`: `label`, `preset`, `attributes`; unset = no link) and
  `CookieConsent.settingsLink` (`CookieConsentSettingsLink`: `label`, `preset`,
  `attributes`, `renderedAttributes`, or null). Preset constants
  `CookieConsentSettingsLink.PRESET_COOKIEMUNCH`, `PRESET_COOKIEBOT`, `PRESET_ONETRUST`,
  `PRESET_CUSTOM`; `preset` stays a string so new values still decode.
- `messaging().deleteChannel(channel, force)` — `force = true` sends `?force=true` to
  remove the email provider even while email-dependent features are on;
  `deleteChannel(channel)` is unchanged and sends no query.
- `ErrorCodes.EMAIL_PROVIDER_IN_USE` — removing the email provider was refused (409);
  the error's `meta.features` lists the email-dependent features that are on.
- `UserSession.lastActiveOrganizationId`, `UserSession.impersonatedBy` and
  `UserSession.idleExpiresAt` (epoch ms, nullable) on `users().listSessions(id)`.

### Changed

- `OAuthClient`, `OAuthClientWithSecret`, `CookieConsent` and `UserSession` gained record
  components; the previous constructors are kept as overloads.

## 1.2.0

### Added

- `sessions().touch(id)` — `POST /v1/sessions/{id}/touch`, backend session keep-alive;
  returns `SessionTouch` (the session plus `extended` and `idleExpiresAt`).
- `Session.idleExpiresAt` (epoch ms, nullable).
- API key spend caps: `ApiKeyLimit` (`unit`, `amount`, `period`: `request` | `day` |
  `month` | `total`), `CreateApiKeyBody.limits`, `UpdateApiKeyBody.limits` and
  `UpdateApiKeyBody.clearLimits()` (sends `"limits": null`), plus `UpdateApiKeyBody.set(key, value)`.
- `ApiKey`, `ApiKeyWithSecret`: `limits` and `usage` (`List<ApiKeyLimitState>`:
  `unit`, `period`, `amount`, `used`, `remaining`, `windowStart`, `resetsAt`).
- `ApiKeyVerification`: `name`, `scopes`, `constraints`, `limits`, `usage`.
- `apiKeys().recordUsage(id, body)` / `recordUsage(id, amount, unit)` —
  `POST /v1/api_keys/{id}/usage`, returns `ApiKeyUsageDebit` (`keyId`, `unit`,
  `amount`, `allowed`, `code`, `exceeded`, `usage`, `counters`, `idempotentReplay`);
  body `RecordApiKeyUsageBody` (`amount`, `unit`, `idempotencyKey`).
- `apiKeys().getUsage(id)` — `GET /v1/api_keys/{id}/usage`, returns `ApiKeyUsage`
  (`keyId`, `limits`, `usage`, `counters`); `ApiKeyUsageCounter` (`unit`, `period`,
  `used`, `windowStart`, `resetsAt`).
- `CreateUserBody.notify` and `ImportExportImportUsersBody.notify`.
- `ApiKeyWebhookData` — the `data` of the `api_key.created`, `api_key.updated`,
  `api_key.revoked` and `api_key.limit_reached` webhook events (`name`, `claims`,
  `scopes`, `constraints`, `limits`, `maxTokenLifetimeSeconds`, `expiresAt`,
  `createdAt`, `updatedAt`, and `limit` on `api_key.limit_reached`).
- `ErrorCodes.LIMIT_EXCEEDED`, `SESSION_REVOKED`, `SESSION_IDLE_EXPIRED`, `SESSION_EXPIRED`.

### Changed

- `ApiKey`, `ApiKeyWithSecret`, `ApiKeyVerification` and `Session` gained record
  components; the previous constructors are kept as overloads.
- `AtlasIdAppCopy.signInTitle` / `signUpTitle` Javadoc states the current defaults
  ("Sign in", "Create your Atlas Account"); no API change.

## 1.1.0

### Added

- Full API coverage: every secret-key and platform-key operation in the public API
  reference now has a typed method. New namespaces: `design()`, `platform()`,
  `machines()`, `connectors()`, `auditEvents()`, `notifications()`, `openFga()`,
  `analytics()`, `diagnostics()`, `configChangelog()`, `redirectUrls()`,
  `entitlementOverrides()`, `orgLdapConnections()`, `sessionExchangeApps()`,
  `organizationSettingsSchema()`, `legacyAuthConnection()`, `mcp()`,
  `mcpConnector()`, `pca()`, `tickets()`, `emailAddresses()`, `captcha()` and
  `testing()`; many new methods on `users()`, `organizations()`, `webhooks()`,
  `billing()`, `apiKeys()`, `waitlist()`, `instance()` and others.
- Platform key support: `AtlasClient.builder().platformKey("psk_...")` and
  `atlas.platform()` for apps, Sign in with Atlas apps (logout URIs, branding,
  design, secret rotation) and account-owned design images.
- Sign-in page design: instance and organization designs, previews, the design
  catalog, and image uploads. The design object is fully typed.
- Raw-bytes uploads (`Transport.requestBytes`) for design images.
- `atlas.instance().preflight()` with typed findings, and cookie-consent settings.
- Strict request bodies: `builder().strict(true)` and per-call
  `atlas.withStrict(...)` send `Atlas-Strict`; ignored fields reported in
  `Atlas-Unknown-Fields` reach `builder().onUnknownFields(...)` (default: a log warning).
- `LogoutTokenVerifier` for OIDC back-channel `logout_token`s.
- OAuth providers: list filters (`provider`, `configured`); `displayName`,
  `iconUrl`, `mode`, `customRedirectUri` and wallet settings on upsert (with a
  `clearSetting` helper); `iconUrl`, `mode` and `settingValues` on the provider.
- Webhooks: update, reveal/rotate secret, test, redeliver, a delivery `limit`,
  `listWithEvents()`, and `failingSince` / `eventType` / `payload` fields.
- Organization policy: typed `allowedSignInMethods`, `ipAllowlist`,
  `maxSessionAgeSeconds` and `endUserPool`, `clear(key)`, and
  `OrganizationPolicy.typed()`.
- `ErrorCodes` constants for common error codes.
- Every generated request body has `set(key, value)` to send any field,
  including an explicit `null` to clear a stored value.

### Changed

- Nulls placed inside maps are now sent as `null` (previously dropped), so a
  `null` value can clear a stored setting. Unset properties are still omitted.
- `OrganizationEntitlements.features` is now a `List<String>`, matching the API
  (it was mistyped as a map), and `limits` was added.
- `AtlasClient` accepts a platform key without a secret key; instance calls then
  fail with a clear message.
