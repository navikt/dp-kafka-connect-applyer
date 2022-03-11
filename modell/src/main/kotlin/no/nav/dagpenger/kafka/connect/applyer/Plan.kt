package no.nav.dagpenger.kafka.connect.applyer

class Plan(eksisterendeConnectors: Set<Connector>, ønskedeConnectors: Set<Connector>) {
    val skalOpprettes = ønskedeConnectors.minus(eksisterendeConnectors).toList()
    val skalOppdateres = eksisterendeConnectors.intersect(ønskedeConnectors).toList()
    val skalSlettes = eksisterendeConnectors.minus(ønskedeConnectors).toList()
}
