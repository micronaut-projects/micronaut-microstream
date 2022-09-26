plugins {
    id("io.micronaut.internal.build.microstream-module")
}

dependencies {
    api(project(":microstream"))
    api(libs.managed.microstream.cache)
    api(mn.micronaut.cache.core)
    testImplementation(mn.micronaut.cache.tck)
    testImplementation(libs.jupiter.api)
}
