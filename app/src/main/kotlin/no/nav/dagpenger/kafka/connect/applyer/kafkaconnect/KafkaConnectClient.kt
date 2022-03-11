package no.nav.dagpenger.kafka.connect.applyer.kafkaconnect

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import no.nav.dagpenger.kafka.connect.applyer.Connector
import no.nav.dagpenger.kafka.connect.applyer.Resultat

internal abstract class KafkaConnectClient {
    companion object {
        val objectMapper = ObjectMapper()
    }

    abstract suspend fun hentConnectors(): Set<Connector>
    abstract suspend fun opprettConnector(connector: Connector): Resultat
    abstract suspend fun oppdaterConnector(connector: Connector): Resultat
    abstract suspend fun slettConnector(connector: Connector): Resultat

    protected fun resultat(status: Boolean, body: String?) =
        KafkaConnectResultat(status, body?.let { objectMapper.readTree(it) })

    class KafkaConnectResultat internal constructor(status: Boolean, private val json: JsonNode?) : Resultat {
        override val suksess = status
    }
}
