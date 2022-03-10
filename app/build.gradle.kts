plugins {
    id("dp.kafka.connect.applyer.kotlin-application-conventions")
}

dependencies {
    implementation(project(":utilities"))
}

application {
    // Define the main class for the application.
    mainClass.set("dp.kafka.connect.applyer.app.AppKt")
}
