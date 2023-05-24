plugins {
    id("io.micronaut.internal.build.microstream-module")
}

dependencies {
    api(projects.micronautMicrostream)
    api(libs.managed.microstream.cache)
    api(mnCache.micronaut.cache.core)
    testImplementation(mnCache.micronaut.cache.tck)
    testImplementation(libs.jupiter.api)
}
