plugins {
    id("io.micronaut.build.internal.module")
}

dependencies {
    api(project(":microstream"))
    api(libs.managed.microstream.cache)
    api(libs.micronaut.cache.core)
    testImplementation(libs.micronaut.cache.tck)
    testImplementation(libs.jupiter.api)
}

micronautBuild {
    binaryCompatibility {
        enabled.set(false)
    }
}
