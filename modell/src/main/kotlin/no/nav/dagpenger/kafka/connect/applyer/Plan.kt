package no.nav.dagpenger.kafka.connect.applyer

class Plan(eksisterendeConnectors: Set<Connector>, √łnskedeConnectors: Set<Connector>) {
    val skalOpprettes = √łnskedeConnectors.minus(eksisterendeConnectors).toList()
    val skalOppdateres = eksisterendeConnectors.intersect(√łnskedeConnectors).toList()
    val skalSlettes = eksisterendeConnectors.minus(√łnskedeConnectors).toList()
}
