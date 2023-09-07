package llesha.plugins;

import llesha.module

import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RoutingKtTest {
    @Test
    fun testGetRnd() = testApplication {
        application {
            module()
        }

        client.get("/rnd/str?length=10").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertTrue(bodyAsText().length == 10)
        }

        client.get("/rnd/str?length=5..10&symbols=A").apply {
            assertEquals(HttpStatusCode.OK, status)
            val body = bodyAsText()
            assertTrue(body.length in 5..10)
            assertTrue(body.matches(Regex("A*")))
        }

        client.get("/rnd/0..5").apply {
            assertEquals(HttpStatusCode.OK, status)
            val body = bodyAsText().toInt()
            assertTrue(body in 0..5)
        }
    }
}