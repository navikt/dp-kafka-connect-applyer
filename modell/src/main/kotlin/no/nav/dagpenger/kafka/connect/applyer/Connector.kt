package no.nav.dagpenger.kafka.connect.applyer

class Connector(val name: String, val config: String) {
    override fun toString(): String {
        return "Connector[name=$name]"
    }

    override fun hashCode() = name.hashCode()

    override fun equals(other: Any?) = other is Connector && name == other.name
}
