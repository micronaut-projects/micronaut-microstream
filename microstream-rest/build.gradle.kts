plugins {
    id("io.micronaut.internal.build.microstream-module")
}

dependencies {
    annotationProcessor(mnValidation.micronaut.validation.processor)
    annotationProcessor(mnSerde.micronaut.serde.processor)

    implementation(libs.managed.microstream.storage.restservice)
    implementation(mnValidation.micronaut.validation)

    implementation(projects.micronautMicrostream)
    implementation(mnSerde.micronaut.serde.jackson)

    testImplementation(mn.micronaut.http.server.netty)
    testImplementation(mn.micronaut.http.client)
    testImplementation(mn.logback.classic)
}
