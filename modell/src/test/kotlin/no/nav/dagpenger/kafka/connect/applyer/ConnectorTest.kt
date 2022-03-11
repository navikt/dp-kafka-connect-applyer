package no.nav.dagpenger.kafka.connect.applyer

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class ConnectorTest {
    @Test
    fun `Connectors er unike i sets`() {
        assertTrue(hashSetOf(Connector("dp-data-inntekt", "{}")).contains(Connector("dp-data-inntekt", "{}")))
        assertEquals(1, hashSetOf(Connector("dp-data-inntekt", "{}"), Connector("dp-data-inntekt", "{}")).size)
        assertEquals(1, setOf(Connector("dp-data-inntekt", "{}"), Connector("dp-data-inntekt", "{}")).size)
    }

    @Test
    fun `Connectors er like via navn`() {
        assertEquals(Connector("dp-data-inntekt", "{}").hashCode(), Connector("dp-data-inntekt", "{}").hashCode())
    }
}
