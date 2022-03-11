package no.nav.dagpenger.kafka.connect.applyer

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import no.nav.dagpenger.kafka.connect.applyer.kafkaconnect.KafkaConnectClient
import no.nav.dagpenger.kafka.connect.applyer.kilder.ConnectorKatalog
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class IntegrasjonsTest {
    private val client = mockk<KafkaConnectClient>(relaxed = true)
    private val ønsketTilstand = mockk<ConnectorKatalog>()

    @BeforeEach
    fun setup() {
        coEvery { client.hentConnectors() } returns
            setOf(connector("skal-slettes"), connector("skal-oppdateres"))

        every { ønsketTilstand.connectors() } returns
            setOf(connector("skal-lages"), connector("skal-oppdateres"))
    }

    @Test
    fun hentConnectors() = runBlocking {
        val forsoner = Forsoner(client)
        forsoner.forson(ønsketTilstand)

        coVerify(exactly = 1) { client.opprettConnector(eq(connector("skal-lages"))) }
        coVerify(exactly = 1) { client.oppdaterConnector(eq(connector("skal-oppdateres"))) }
        coVerify(exactly = 1) { client.slettConnector(eq(connector("skal-slettes"))) }
    }

    private fun connector(navn: String) = Connector(navn, "{}")
}
