plugins {
    id("io.micronaut.build.internal.module")
}

dependencies {
    api(project(":microstream"))
    api(libs.managed.microstream.cache)
    api(mn.micronaut.cache.core)
    testImplementation(mn.micronaut.cache.tck)
    testImplementation(libs.jupiter.api)
}
