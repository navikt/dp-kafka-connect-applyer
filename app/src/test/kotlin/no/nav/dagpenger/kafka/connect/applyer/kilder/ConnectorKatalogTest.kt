package no.nav.dagpenger.kafka.connect.applyer.kilder

import com.google.common.jimfs.Configuration
import com.google.common.jimfs.Jimfs
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.nio.file.Files
import kotlin.text.Charsets.UTF_8

internal class ConnectorKatalogTest {
    private val fileSystem = Jimfs.newFileSystem(Configuration.unix())
    private val connectorsDir = fileSystem.getPath("")

    @BeforeEach
    fun setup() {
        Files.write(connectorsDir.resolve("connector-1.json"), listOf("{}"), UTF_8)
        Files.write(connectorsDir.resolve("connector-2.json"), listOf("""{ "foo": "bar" }"""), UTF_8)
        Files.write(connectorsDir.resolve("not-connector.txt"), listOf("""{ "foo": "bar" }"""), UTF_8)
    }

    @Test
    fun `Finner connector-definisjoner på disk`() {
        val source = ConnectorKatalog(connectorsDir)
        assertEquals(2, source.connectors().size)
        assertEquals(2, source.connectors().size)

        with(source.connectors().first()) {
            assertEquals("connector-1", name)
            assertEquals("{}", config)
        }
    }
}

