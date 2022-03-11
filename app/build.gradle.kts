plugins {
    id("dp.kafka.connect.applyer.kotlin-application-conventions")
}

repositories {
    maven("https://jitpack.io")
}

dependencies {
    val ktorVersion = "2.0.0-beta-1"
    implementation(project(":modell"))

    implementation("com.github.navikt:rapids-and-rivers:2022.02.28-16.20.1a549dcffaae")
    implementation("io.github.microutils:kotlin-logging-jvm:2.1.20")

    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-jackson:$ktorVersion")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.1")

    testImplementation("io.mockk:mockk:1.12.3")
    testImplementation("io.ktor:ktor-client-mock:$ktorVersion")
    testImplementation("com.google.jimfs:jimfs:1.2")
}

application {
    mainClass.set("no.nav.dagpenger.kafka.connect.applyer.AppKt")
}
