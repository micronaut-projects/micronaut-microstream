plugins {
    id("io.micronaut.build.internal.module")
}

dependencies {
    annotationProcessor(libs.micronaut.validation)

    api(libs.micronaut.serde.api)

    implementation(libs.managed.microstream.storage.restservice)
    implementation(libs.micronaut.validation)
    implementation(libs.micronaut.router)
    implementation(project(":microstream"))
    implementation(libs.micronaut.jackson.databind)
    testImplementation(libs.micronaut.http.server.netty)
    testImplementation(libs.micronaut.http.client)
    testImplementation(libs.micronaut.management)
}

micronautBuild {
    binaryCompatibility {
        enabled.set(false)
    }
}
