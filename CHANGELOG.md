# Changelog

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
