package no.nav.dagpenger.kafka.connect.applyer

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class PlanTest {
    @Test
    fun `kan lage planer`() {
        val plan = Plan(setOf(), setOf())
        assertEquals(0, plan.skalOpprettes.size)
        assertEquals(0, plan.skalOppdateres.size)
        assertEquals(0, plan.skalSlettes.size)
    }

    @Test
    fun `Kan lage planer for opprettelse, endre, og slette`() {
        val søknad = Connector("dp-data-søknader", "{}")
        val inntekt = Connector("dp-data-inntekt", "{}")
        val vedtak = Connector("dp-data-vedtak", "{}")
        val plan = Plan(
            eksisterendeConnectors = setOf(inntekt, søknad), ønskedeConnectors = setOf(vedtak, søknad)
        )
        with(plan.skalOpprettes) {
            assertEquals(1, size)
            assertEquals(listOf(vedtak), this)
        }

        with(plan.skalSlettes) {
            assertEquals(1, size)
            assertEquals(listOf(inntekt), this)
        }

        with(plan.skalOppdateres) {
            assertEquals(1, size)
            assertEquals(listOf(søknad), this)
        }
    }
}
