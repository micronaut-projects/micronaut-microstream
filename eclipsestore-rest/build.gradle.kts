plugins {
    id("io.micronaut.internal.build.eclipsestore-module")
}

dependencies {
    annotationProcessor(mnValidation.micronaut.validation.processor)
    annotationProcessor(mnSerde.micronaut.serde.processor)

    implementation(libs.managed.eclipsestore.storage.restservice)
    implementation(mnValidation.micronaut.validation)

    implementation(projects.micronautEclipsestore)
    implementation(mnSerde.micronaut.serde.api)
    implementation(mnSerde.micronaut.serde.jackson)

    testImplementation(mn.micronaut.http.server.netty)
    testImplementation(mn.micronaut.http.client)
    testImplementation(mnLogging.logback.classic)
}
