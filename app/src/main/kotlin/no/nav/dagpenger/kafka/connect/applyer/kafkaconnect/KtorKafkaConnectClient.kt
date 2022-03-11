package no.nav.dagpenger.kafka.connect.applyer.kafkaconnect

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.ContentNegotiation
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.plugin
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.encodedPath
import io.ktor.serialization.jackson.jackson
import mu.KotlinLogging
import no.nav.dagpenger.kafka.connect.applyer.Connector
import java.util.UUID

private val logger = KotlinLogging.logger {}

internal class KtorKafkaConnectClient(
    endpointUrl: String,
    engine: HttpClientEngine,
    retries: Int
) : KafkaConnectClient() {
    constructor(endpointUrl: String) : this(endpointUrl, CIO.create(), 5)

    private val client = HttpClient(engine) {
        install(ContentNegotiation) {
            jackson()
        }
        install(HttpRequestRetry) {
            maxRetries = retries
            retryIf { _, response -> response.status == HttpStatusCode.Conflict }
            modifyRequest { request ->
                logger.info { "Retry $retryCount for ${request.method.value} ${request.url.encodedPath}" }
            }
        }

        expectSuccess = false
        defaultRequest {
            url(endpointUrl)
            header(HttpHeaders.Accept, "application/json")
            header("X-Call-Id", UUID.randomUUID().toString())
        }
    }.apply(logRequest())

    override suspend fun hentConnectors(): Set<Connector> {
        return client.get("/connectors").body<List<String>>().map { Connector(it, "{}") }.toSet()
    }

    override suspend fun opprettConnector(connector: Connector) =
        client.put("/connectors/${connector.name}/config") {
            setBody(connector.config)
        }.let { resultat(it.status == HttpStatusCode.Created, it.bodyAsText()) }

    override suspend fun oppdaterConnector(connector: Connector) =
        client.put("/connectors/${connector.name}/config") {
            setBody(connector.config)
        }.let { resultat(it.status == HttpStatusCode.OK, it.bodyAsText()) }

    override suspend fun slettConnector(connector: Connector) =
        client.delete("/connectors/${connector.name}")
            .run { resultat(status == HttpStatusCode.NoContent, null) }

    private fun logRequest(): HttpClient.() -> Unit = {
        plugin(HttpSend).intercept { request ->
            logger.info { "Request ${request.method.value} ${request.url.encodedPath}" }
            execute(request).also {
                logger.info { "Response ${it.response.status.value} for ${request.method.value} ${request.url.encodedPath}" }
            }
        }
    }
}
