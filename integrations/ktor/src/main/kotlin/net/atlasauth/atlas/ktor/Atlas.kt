package net.atlasauth.atlas.ktor

import io.ktor.http.HttpStatusCode
import io.ktor.http.auth.HttpAuthHeader
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.createRouteScopedPlugin
import io.ktor.server.auth.AuthenticationChecked
import io.ktor.server.auth.AuthenticationConfig
import io.ktor.server.auth.bearer
import io.ktor.server.auth.principal
import io.ktor.server.response.respond
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.atlasauth.atlas.verify.AccessTokenVerifier
import net.atlasauth.atlas.verify.ProtectCondition
import net.atlasauth.atlas.verify.SessionClaims
import net.atlasauth.atlas.verify.SessionVerifier

/**
 * The verified Atlas credential on a call: a session ([claims]) or an OAuth access token ([access]).
 * Read it with `call.principal<AtlasPrincipal>()` or [atlas].
 */
class AtlasPrincipal(val token: String, val claims: SessionClaims?, val access: AccessTokenVerifier.Result? = null) {
    val userId: String get() = claims?.sub() ?: access!!.subject()
    val sessionId: String? get() = claims?.sid()
    val orgId: String? get() = claims?.orgId()
    val orgRole: String? get() = claims?.orgRole()
    /** True for an OAuth access token (an API client), false for a person's session. */
    val isAccessToken: Boolean get() = access != null
    /** The scopes of an access token; empty for a session. */
    val scopes: List<String> get() = access?.scopes() ?: emptyList()
    /** The OAuth client an access token was issued to. */
    val clientId: String? get() = access?.clientId()

    fun hasScopes(required: Collection<String>): Boolean = scopes.containsAll(required)

    /** True when the session satisfies the condition. An access token has no organization claims, so only an empty condition passes. */
    fun has(condition: ProtectCondition): Boolean =
        if (claims != null) SessionVerifier.has(claims, condition) else condition.isEmpty
}

/** Settings for [atlas]. `issuer` and `jwksUrl` (or a ready `verifier`) are required. */
class AtlasConfig {
    /** Expected `iss` of the instance's tokens. */
    var issuer: String? = null
    /** The instance's JWKS URL; keys are cached and verification is local. */
    var jwksUrl: String? = null
    /** Optional `azp` allowlist: refuse tokens minted for another of your apps. Set it. */
    var authorizedParties: Collection<String>? = null
    /** Session cookie read when no `Authorization: Bearer` header is present. */
    var cookieName: String = "__session"
    var realm: String = "Atlas"
    /** A prebuilt verifier (tests, legacy issuers, custom HTTP client). */
    var verifier: SessionVerifier? = null
    /**
     * Also accept OAuth access tokens (`Authorization: Bearer` only, never the cookie): the token's `aud` must contain
     * [accessTokenAudience]. Null (default) accepts sessions only.
     */
    var accessTokenAudience: String? = null
    /** Scopes every access token must carry; a token without them is refused. */
    var accessTokenRequiredScopes: List<String> = emptyList()
    /** A prebuilt access-token verifier (tests, introspection, DPoP). */
    var accessTokenVerifier: AccessTokenVerifier? = null

    internal fun buildAccess(): AccessTokenVerifier? = accessTokenVerifier ?: accessTokenAudience?.let { aud ->
        val iss = requireNotNull(issuer?.takeIf { it.isNotEmpty() }) { "atlas { } needs `issuer`." }
        AccessTokenVerifier.builder().issuer(iss).audience(aud).apply {
            jwksUrl?.let { jwksUrl(it) }
            if (accessTokenRequiredScopes.isNotEmpty()) requiredScopes(*accessTokenRequiredScopes.toTypedArray())
        }.build()
    }

    internal fun build(): SessionVerifier = verifier ?: run {
        val iss = requireNotNull(issuer?.takeIf { it.isNotEmpty() }) { "atlas { } needs `issuer`." }
        val jwks = requireNotNull(jwksUrl?.takeIf { it.isNotEmpty() }) { "atlas { } needs `jwksUrl`." }
        SessionVerifier.builder().issuer(iss).jwksUrl(jwks).authorizedParties(authorizedParties).build()
    }
}

private val BEARER = Regex("^Bearer\\s+(\\S+)$", RegexOption.IGNORE_CASE)

/**
 * Registers the Atlas authentication provider:
 *
 * ```kotlin
 * install(Authentication) {
 *     atlas { issuer = "https://auth.example.com"; jwksUrl = "https://auth.example.com/.well-known/jwks.json" }
 * }
 * routing { authenticate("atlas") { get("/me") { call.respondText(call.atlas!!.userId) } } }
 * ```
 *
 * `Authorization: Bearer` wins over the session cookie. Anything that is not a verified token is
 * refused with 401 (fail closed).
 */
fun AuthenticationConfig.atlas(name: String? = "atlas", configure: AtlasConfig.() -> Unit) {
    val config = AtlasConfig().apply(configure)
    val verifier = config.build()
    val accessVerifier = config.buildAccess()
    bearer(name) {
        realm = config.realm
        authHeader { call ->
            val bearer = call.request.headers["Authorization"]?.let { BEARER.matchEntire(it.trim())?.groupValues?.get(1) }
            val token = bearer ?: call.request.cookies[config.cookieName]?.takeIf { it.isNotEmpty() }
            token?.let { HttpAuthHeader.Single("Bearer", it) }
        }
        authenticate { credential ->
            try {
                val fromHeader = request.headers["Authorization"]?.let { BEARER.matchEntire(it.trim())?.groupValues?.get(1) } == credential.token
                // Verification is blocking (it may fetch the key set once a minute).
                withContext(Dispatchers.IO) {
                    val result = verifier.verify(credential.token)
                    if (result.ok()) return@withContext AtlasPrincipal(credential.token, result.claims())
                    // A session did not verify: it may be an OAuth access token for this API.
                    val access = if (fromHeader) accessVerifier?.verify("Bearer ${credential.token}") else null
                    // token_use is required here: a token that does not say it is an access token is never one.
                    if (access != null && access.ok() && access.claims()["token_use"] == "access_token") {
                        AtlasPrincipal(credential.token, null, access)
                    } else null
                }
            } catch (_: Exception) {
                null
            }
        }
    }
}

/** The verified Atlas session of this call, or null. */
val ApplicationCall.atlas: AtlasPrincipal? get() = principal<AtlasPrincipal>()

class AtlasRequireConfig {
    var condition: ProtectCondition = ProtectCondition.none()
    /** Scopes an access token must carry (403 otherwise). A session has no scopes and never passes a non-empty list. */
    var scopes: List<String> = emptyList()
}

/**
 * Route-scoped authorization. Install it inside `authenticate("atlas") { }`; a session that does not
 * satisfy the condition gets 403, and a call with no Atlas session at all gets 401.
 *
 * ```kotlin
 * authenticate("atlas") {
 *     route("/billing") { install(AtlasRequire) { condition = ProtectCondition().permission("org:billing:read") } }
 * }
 * ```
 */
val AtlasRequire = createRouteScopedPlugin("AtlasRequire", ::AtlasRequireConfig) {
    val condition = pluginConfig.condition
    val scopes = pluginConfig.scopes
    on(AuthenticationChecked) { call ->
        val principal = call.principal<AtlasPrincipal>()
        when {
            principal == null -> call.respond(HttpStatusCode.Unauthorized)
            !principal.has(condition) || !principal.hasScopes(scopes) -> call.respond(HttpStatusCode.Forbidden)
        }
    }
}
