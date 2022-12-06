import org.gradle.api.internal.artifacts.dependencies.DefaultDependencyConstraint.strictly

plugins {
    id("io.micronaut.internal.build.microstream-module")
}

dependencies {
    api(project(":microstream"))
    api(libs.managed.microstream.cache)
    api(mnCache.micronaut.cache.core)
    testImplementation(mnCache.micronaut.cache.tck)
    testImplementation(libs.jupiter.api)
}
