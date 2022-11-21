plugins {
    id("io.micronaut.internal.build.microstream-module")
}

dependencies {
    annotationProcessor(mn.micronaut.validation)
    annotationProcessor(mnSerde.micronaut.serde.processor)

    implementation(libs.managed.microstream.storage.restservice)
    implementation(mn.micronaut.validation)

    implementation(project(":microstream"))
    implementation(mnSerde.micronaut.serde.jackson)

    testImplementation(mn.micronaut.http.server.netty)
    testImplementation(mn.micronaut.http.client)
    testImplementation(libs.logback.classic)
}
