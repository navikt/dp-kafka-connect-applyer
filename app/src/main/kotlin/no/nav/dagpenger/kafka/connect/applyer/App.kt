package no.nav.dagpenger.kafka.connect.applyer

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import no.nav.dagpenger.kafka.connect.applyer.kafkaconnect.KtorKafkaConnectClient
import no.nav.dagpenger.kafka.connect.applyer.kilder.ConnectorKatalog
import no.nav.helse.rapids_rivers.RapidApplication
import no.nav.helse.rapids_rivers.RapidsConnection

fun main(): Unit = runBlocking {
    val env = System.getenv()
    val kafkaConnect = KtorKafkaConnectClient(getEnv("KAFKA_CONNECT_URL"))
    val ønsketTilstand = ConnectorKatalog(getEnv("CONNECTOR_DIR"))
    val forsoner = Forsoner(kafkaConnect)

    RapidApplication.create(env).apply {
        register(object : RapidsConnection.StatusListener {
            override fun onStartup(rapidsConnection: RapidsConnection) {
                launch {
                    forsoner.forson(ønsketTilstand)
                }
            }
        })
    }.start()
}

private fun getEnv(name: String) = requireNotNull(System.getenv(name)) { "Forventer $name" }
