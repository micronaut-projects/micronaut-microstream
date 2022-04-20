plugins {
    id("io.micronaut.build.internal.module")
}

dependencies {
    api(libs.managed.microstream.storage.embedded.configuration)

    implementation(libs.micronaut.aop)
}

micronautBuild {
    binaryCompatibility {
        enabled.set(false)
    }
}
