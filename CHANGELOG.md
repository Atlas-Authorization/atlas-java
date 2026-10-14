# Changelog

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
