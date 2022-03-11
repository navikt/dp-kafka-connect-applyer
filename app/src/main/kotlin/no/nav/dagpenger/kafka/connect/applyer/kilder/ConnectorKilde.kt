package no.nav.dagpenger.kafka.connect.applyer.kilder

import no.nav.dagpenger.kafka.connect.applyer.Connector

interface ConnectorKilde {
    fun connectors(): Set<Connector>
}
