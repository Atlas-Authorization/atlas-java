# Changelog

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
