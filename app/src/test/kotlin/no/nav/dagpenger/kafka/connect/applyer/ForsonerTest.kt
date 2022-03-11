package no.nav.dagpenger.kafka.connect.applyer

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import no.nav.dagpenger.kafka.connect.applyer.kafkaconnect.KafkaConnectClient
import no.nav.dagpenger.kafka.connect.applyer.kilder.ConnectorKatalog
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ForsonerTest {
    private val kafkaConnectClient = mockk<KafkaConnectClient>(relaxed = true)
    private val ønsketTilstand = mockk<ConnectorKatalog>()
    private val forsoner = Forsoner(kafkaConnectClient)

    @Test
    fun `Forsoner forskjell mellom ønsket og faktisk tilstand `() = runBlocking {
        coEvery {
            kafkaConnectClient.hentConnectors()
        } returns setOf(Connector("gammel", "{}"), Connector("oppdateres", "{}"))
        every { ønsketTilstand.connectors() } returns setOf(Connector("ny", "{}"), Connector("oppdateres", "{}"))

        forsoner.forson(ønsketTilstand)

        coVerify {
            kafkaConnectClient.opprettConnector(eq(Connector("ny", "{}")))
            kafkaConnectClient.oppdaterConnector(eq(Connector("oppdateres", "{}")))
            kafkaConnectClient.slettConnector(eq(Connector("gammel", "{}")))
        }
    }

    @Test
    fun `Håndterer feil fra Kafka Connect`() = runBlocking {
        coEvery { kafkaConnectClient.hentConnectors() } returns emptySet()
        coEvery {
            kafkaConnectClient.opprettConnector(any())
        } returnsMany listOf(
            mockk<Resultat>().apply { every { suksess } returns false },
        )
        every { ønsketTilstand.connectors() } returns setOf(Connector("ny", "{}"), Connector("oppdateres", "{}"))

        assertThrows<Exception> {
            forsoner.forson(ønsketTilstand)
        }
    }
}
