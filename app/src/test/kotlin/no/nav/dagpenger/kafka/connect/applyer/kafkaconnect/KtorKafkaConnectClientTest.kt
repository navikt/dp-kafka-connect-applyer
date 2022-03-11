package no.nav.dagpenger.kafka.connect.applyer.kafkaconnect

import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.runBlocking
import no.nav.dagpenger.kafka.connect.applyer.Connector
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class KtorKafkaConnectClientTest {
    @Test
    fun hentConnectors() = runBlocking {
        val mockEngine = MockEngine { _ ->
            respond(
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
                content = """[
                  "my-source",
                  "my-sink"
                ]
                """.trimIndent(),
            )
        }
        with(KtorKafkaConnectClient("http://localhost", mockEngine, 5)) {
            val connectors = hentConnectors()

            assertEquals(
                setOf(
                    Connector("my-source", "{}"),
                    Connector("my-sink", "{}")
                ),
                connectors
            )
        }
    }

    @Test
    fun opprettConnector() = runBlocking {
        val mockEngine = MockEngine { _ ->
            respond(
                status = HttpStatusCode.Created,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
                content = """{"foo":"bar"}""".trimIndent(),
            )
        }
        with(KtorKafkaConnectClient("http://localhost", mockEngine, 5)) {
            val resultat = opprettConnector(Connector("ny", "{}"))
            assertTrue(resultat.suksess)
        }
    }

    @Test
    fun oppdaterConnector() = runBlocking {
        val mockEngine = MockEngine { _ ->
            respond(
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
                content = """{"foo":"bar"}"""
            )
        }
        with(KtorKafkaConnectClient("http://localhost", mockEngine, 5)) {
            val resultat = oppdaterConnector(Connector("ny", "{}"))
            assertTrue(resultat.suksess)
        }
    }

    @Test
    fun slettConnector() = runBlocking {
        val mockEngine = MockEngine { _ ->
            respond(status = HttpStatusCode.NoContent, content = "")
        }
        with(KtorKafkaConnectClient("http://localhost", mockEngine, 5)) {
            val resultat = slettConnector(Connector("ny", "{}"))
            assertTrue(resultat.suksess)
        }
    }

    @Test
    fun `alle håndterer 409 Conflict`() = runBlocking {
        val mockEngine = MockEngine { _ ->
            respond(status = HttpStatusCode.Conflict, content = "")
        }
        with(KtorKafkaConnectClient("http://localhost", mockEngine, 0)) {
            assertFalse(opprettConnector(Connector("ny", "{}")).suksess)
            assertFalse(oppdaterConnector(Connector("ny", "{}")).suksess)
            assertFalse(slettConnector(Connector("ny", "{}")).suksess)
        }
    }
}
