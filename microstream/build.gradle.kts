plugins {
    id("io.micronaut.build.internal.module")
}

dependencies {
    compileOnly(libs.micronaut.micrometer.core)
    api(libs.managed.microstream.storage.embedded.configuration)

    compileOnly(libs.micronaut.management)
    implementation(libs.projectreactor)

    testImplementation(libs.projectreactor.test)
    testImplementation(libs.micronaut.micrometer.core)
    testImplementation(libs.micronaut.management)
    testImplementation(libs.micronaut.http.server.netty)
    testImplementation(libs.micronaut.http.client)
}

micronautBuild {
    binaryCompatibility {
        enabled.set(false)
    }
}
