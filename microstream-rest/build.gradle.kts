plugins {
    id("io.micronaut.build.internal.module")
}

dependencies {
    annotationProcessor(mn.micronaut.validation)

    implementation(libs.managed.microstream.storage.restservice)
    implementation(mn.micronaut.validation)

    implementation(project(":microstream"))
    implementation(mn.micronaut.jackson.databind)

    testImplementation(mn.micronaut.http.server.netty)
    testImplementation(mn.micronaut.http.client)
    testImplementation(libs.logback.classic)
}
