package no.nav.dagpenger.kafka.connect.applyer.kilder

import no.nav.dagpenger.kafka.connect.applyer.Connector
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.absolute
import kotlin.io.path.extension
import kotlin.io.path.nameWithoutExtension
import kotlin.io.path.readText

internal class ConnectorKatalog internal constructor(private val connectorDir: Path) : ConnectorKilde {
    constructor(dir: String) : this(Path.of(dir).absolute())

    private val stream get() = Files.list(connectorDir)

    override fun connectors(): Set<Connector> = stream.use { p ->
        p.filter(::isConnector).map(::connector).toList()
    }.toSet()

    private fun connector(it: Path) = Connector(it.nameWithoutExtension, it.readText().trim())
    private fun isConnector(it: Path) = it.extension == "json"
}
