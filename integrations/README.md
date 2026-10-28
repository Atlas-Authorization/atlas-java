# Atlas JVM framework integrations

Ktor, Quarkus and Micronaut integrations on top of `atlas-java`. All three verify Atlas session tokens the same way the SDK does: locally against the cached key set (no call to Atlas per request), issuer check, expiry with 5 seconds of clock skew, and an optional `azp` allowlist. Set `authorized-parties`: it stops a token issued to one of your apps being replayed against another. A request that does not carry a verified token is refused with 401 (fail closed); a verified session that lacks a required permission or role gets 403.

| Module | Artifact | Idiom |
| --- | --- | --- |
| [ktor](ktor) | `net.atlasauth:atlas-ktor` | `install(Authentication) { atlas { ... } }`, `authenticate("atlas") { }`, `call.atlas`, `install(AtlasRequire)` |
| [quarkus](quarkus) | `net.atlasauth:atlas-quarkus` | `@AtlasAuthenticated` / `@AtlasRequires` on Jakarta REST resources, config via `atlas.*` |
| [micronaut](micronaut) | `net.atlasauth:atlas-micronaut` | Micronaut Security `TokenReader` + `TokenValidator`; `@Secured` works |

Build: `mvn -B install -DskipTests` in the parent directory (installs `atlas-java`), then `mvn -B test` here.
