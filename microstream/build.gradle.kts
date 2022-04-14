plugins {
    id("io.micronaut.build.internal.module")
}

dependencies {
    api(libs.managed.microstream.storage.embedded.configuration)
}

micronautBuild {
    binaryCompatibility {
        enabled.set(false)
    }
}
