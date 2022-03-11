package no.nav.dagpenger.kafka.connect.applyer

import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import no.nav.dagpenger.kafka.connect.applyer.kafkaconnect.KafkaConnectClient
import no.nav.dagpenger.kafka.connect.applyer.kilder.ConnectorKilde

private val logger = KotlinLogging.logger {}

internal class Forsoner constructor(
    private val client: KafkaConnectClient,
) {
    suspend fun forson(kilde: ConnectorKilde) = forson(kilde.connectors()).also {
        logger.info {
            """
            Kafka Connect er i synk med kilde.
            Connectors fra kilde: ${kilde.connectors()}
            Opprettet: ${it.skalOpprettes.size}
            Oppdaterte: ${it.skalOppdateres.size}
            Slettet: ${it.skalSlettes.size}
            Connectors nå definert i Kafka Connect: "${runBlocking { client.hentConnectors() }}"
            """.trimIndent()
        }
    }

    private suspend fun forson(ønsketTilstand: Set<Connector>): Plan {
        val eksisterendeTilstand = client.hentConnectors().also { connectors ->
            logger.info { "Fant ${connectors.size} connectors: ${connectors.map { it.name }}" }
        }
        val plan = Plan(eksisterendeTilstand, ønsketTilstand)

        plan.skalOpprettes.forEach { connector ->
            client.opprettConnector(connector).also {
                when (it.suksess) {
                    true -> logger.info { "Opprettet connector: $connector" }
                    false -> logger.error(RuntimeException("Kunne ikke opprette: $connector")) { "Kunne ikke opprette connector: $connector" }
                }
            }
        }
        plan.skalOppdateres.forEach { connector ->
            client.oppdaterConnector(connector).also {
                when (it.suksess) {
                    true -> logger.info { "Oppdaterte connector: $connector" }
                    false -> logger.error(RuntimeException("Kunne ikke oppdatere: $connector")) { "Kunne ikke oppdatere connector: $connector" }
                }
            }
        }
        plan.skalSlettes.forEach { connector ->
            client.slettConnector(connector).also {
                when (it.suksess) {
                    true -> logger.info { "Slettet connector: $connector" }
                    false -> logger.error(RuntimeException("Kunne ikke slette: $connector")) { "Kunne ikke slette connector: $connector" }
                }
            }
        }

        return plan
    }
}

internal interface Resultat {
    val suksess: Boolean
}
