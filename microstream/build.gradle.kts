plugins {
    id("io.micronaut.build.internal.module")
}

dependencies {
    api(libs.microstream.storage.embedded.configuration)
}

micronautBuild {
    binaryCompatibility {
        enabled.set(false)
    }
}
