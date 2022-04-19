plugins {
    id("io.micronaut.build.internal.module")
}

dependencies {
    api(libs.micronaut.micrometer.core)
    api(libs.managed.microstream.storage.embedded.configuration)

    testImplementation(project(":microstream"))
    testImplementation(libs.micronaut.management)
    testImplementation(libs.micronaut.http.server.netty)
    testImplementation(libs.micronaut.http.client)
    testImplementation(libs.groovy.json)
}

micronautBuild {
    binaryCompatibility {
        enabled.set(false)
    }
}
