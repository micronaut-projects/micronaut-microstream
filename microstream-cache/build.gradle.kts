plugins {
    id("io.micronaut.build.internal.module")
}

dependencies {
    implementation(project(":microstream"))
    implementation(libs.managed.microstream.cache)
    implementation(libs.micronaut.cache.core)
    testImplementation(libs.micronaut.cache.tck)
    testImplementation(libs.jupiter.api)
}

micronautBuild {
    binaryCompatibility {
        enabled.set(false)
    }
}
