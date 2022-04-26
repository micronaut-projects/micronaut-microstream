plugins {
    id("io.micronaut.build.internal.module")
}

dependencies {
    compileOnly(libs.micronaut.micrometer.core)
    compileOnly(project(":microstream-annotations"))
    api(libs.managed.microstream.storage.embedded.configuration)
    api(libs.managed.microstream.cache)

    implementation(libs.micronaut.cache.core)

    compileOnly(libs.micronaut.management)
    implementation(libs.projectreactor)

    testImplementation(libs.projectreactor.test)
    testImplementation(libs.micronaut.micrometer.core)
    testImplementation(libs.micronaut.management)
    testImplementation(libs.micronaut.http.server.netty)
    testImplementation(libs.micronaut.http.client)
    testImplementation(libs.micronaut.cache.tck)
    testImplementation(libs.jupiter.api)
    testImplementation(project(":microstream-annotations"))
}

micronautBuild {
    binaryCompatibility {
        enabled.set(false)
    }
}
