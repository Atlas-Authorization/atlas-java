package net.atlasauth.atlas.ktor

import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.crypto.RSASSASigner
import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import com.sun.net.httpserver.HttpServer
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.authenticate
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import io.ktor.server.testing.testApplication
import java.net.InetSocketAddress
import java.util.Date
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import net.atlasauth.atlas.verify.ProtectCondition

class AtlasTest {
    private val issuer = "https://auth.example.com"
    private val key: RSAKey = RSAKeyGenerator(2048).keyID("k1").generate()
    private val server: HttpServer = HttpServer.create(InetSocketAddress("127.0.0.1", 0), 0).apply {
        val body = JWKSet(key.toPublicJWK()).toString().toByteArray()
        createContext("/jwks") { ex ->
            ex.sendResponseHeaders(200, body.size.toLong())
            ex.responseBody.use { it.write(body) }
        }
        start()
    }
    private val jwksUrl = "http://127.0.0.1:${server.address.port}/jwks"

    @AfterTest fun stop() = server.stop(0)

    private fun token(iss: String = issuer, azp: String? = null, ttlMs: Long = 60_000, extra: Map<String, Any> = emptyMap()): String {
        val now = System.currentTimeMillis()
        val c = JWTClaimsSet.Builder().issuer(iss).subject("user_1").claim("sid", "sess_1")
            .issueTime(Date(now - 1000)).expirationTime(Date(now + ttlMs))
        azp?.let { c.claim("azp", it) }
        extra.forEach { (k, v) -> c.claim(k, v) }
        return SignedJWT(JWSHeader.Builder(JWSAlgorithm.RS256).keyID("k1").build(), c.build())
            .also { it.sign(RSASSASigner(key)) }.serialize()
    }

    private fun accessToken(aud: String = "https://api.example.com", scope: String = "orders:read", tokenUse: String? = "access_token", ttlMs: Long = 60_000): String {
        val now = System.currentTimeMillis()
        val c = JWTClaimsSet.Builder().issuer(issuer).subject("client_sub").audience(aud).claim("scope", scope).claim("client_id", "cli_1")
            .issueTime(Date(now - 1000)).expirationTime(Date(now + ttlMs))
        tokenUse?.let { c.claim("token_use", it) }
        return SignedJWT(JWSHeader.Builder(JWSAlgorithm.RS256).keyID("k1").type(com.nimbusds.jose.JOSEObjectType("at+jwt")).build(), c.build())
            .also { it.sign(RSASSASigner(key)) }.serialize()
    }

    private fun io.ktor.server.testing.ApplicationTestBuilder.app() = application {
        install(Authentication) {
            atlas {
                issuer = this@AtlasTest.issuer; jwksUrl = this@AtlasTest.jwksUrl; authorizedParties = listOf("https://app.example.com")
                accessTokenAudience = "https://api.example.com"
            }
        }
        routing {
            authenticate("atlas") {
                get("/me") { call.respondText(call.atlas!!.userId) }
                route("/orders") {
                    install(AtlasRequire) { scopes = listOf("orders:read") }
                    get { call.respondText("${call.atlas!!.clientId}:${call.atlas!!.userId}") }
                }
                route("/billing") {
                    install(AtlasRequire) { condition = ProtectCondition().permission("org:billing:read") }
                    get { call.respondText("ok") }
                }
            }
        }
    }

    @Test fun `valid bearer token reaches the route`() = testApplication {
        app()
        val r = client.get("/me") { header("Authorization", "Bearer ${token(azp = "https://app.example.com")}") }
        assertEquals(HttpStatusCode.OK, r.status)
        assertEquals("user_1", r.bodyAsText())
    }

    @Test fun `session cookie is read when there is no header`() = testApplication {
        app()
        val r = client.get("/me") { header("Cookie", "a=b; __session=${token(azp = "https://app.example.com")}") }
        assertEquals(HttpStatusCode.OK, r.status)
    }

    @Test fun `missing, expired, wrong issuer and wrong azp are 401`() = testApplication {
        app()
        assertEquals(HttpStatusCode.Unauthorized, client.get("/me").status)
        for (t in listOf(
            token(azp = "https://app.example.com", ttlMs = -60_000),
            token(iss = "https://evil.example.com", azp = "https://app.example.com"),
            token(azp = "https://other.example.com"),
            "not.a.jwt",
        )) {
            assertEquals(HttpStatusCode.Unauthorized, client.get("/me") { header("Authorization", "Bearer $t") }.status)
        }
    }

    @Test fun `AtlasRequire answers 403 without the permission and 200 with it`() = testApplication {
        app()
        val without = token(azp = "https://app.example.com")
        assertEquals(HttpStatusCode.Forbidden, client.get("/billing") { header("Authorization", "Bearer $without") }.status)
        val with = token(azp = "https://app.example.com", extra = mapOf("org_id" to "o1", "org_role" to "admin", "org_permissions" to listOf("org:billing:read")))
        assertEquals(HttpStatusCode.OK, client.get("/billing") { header("Authorization", "Bearer $with") }.status)
    }

    @Test fun `access tokens are accepted with audience and scope and expose client and scopes`() = testApplication {
        app()
        val r = client.get("/orders") { header("Authorization", "Bearer ${accessToken()}") }
        assertEquals(HttpStatusCode.OK, r.status)
        assertEquals("cli_1:client_sub", r.bodyAsText())
    }

    @Test fun `access tokens with another audience, no token_use, expired or missing scope are refused`() = testApplication {
        app()
        assertEquals(HttpStatusCode.Unauthorized, client.get("/orders") { header("Authorization", "Bearer ${accessToken(aud = "https://other.example.com")}") }.status)
        assertEquals(HttpStatusCode.Unauthorized, client.get("/orders") { header("Authorization", "Bearer ${accessToken(tokenUse = null)}") }.status)
        assertEquals(HttpStatusCode.Unauthorized, client.get("/orders") { header("Authorization", "Bearer ${accessToken(ttlMs = -60_000)}") }.status)
        assertEquals(HttpStatusCode.Forbidden, client.get("/orders") { header("Authorization", "Bearer ${accessToken(scope = "other")}") }.status)
    }

    @Test fun `a session cannot pass a scope gate and an access token cannot pass a permission gate`() = testApplication {
        app()
        val session = token(azp = "https://app.example.com")
        assertEquals(HttpStatusCode.Forbidden, client.get("/orders") { header("Authorization", "Bearer $session") }.status)
        assertEquals(HttpStatusCode.Forbidden, client.get("/billing") { header("Authorization", "Bearer ${accessToken()}") }.status)
    }

    @Test fun `an access token in the cookie is not accepted`() = testApplication {
        app()
        assertEquals(HttpStatusCode.Unauthorized, client.get("/orders") { header("Cookie", "__session=${accessToken()}") }.status)
    }
}
