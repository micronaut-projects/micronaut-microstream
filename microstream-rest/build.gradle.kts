plugins {
    id("io.micronaut.build.internal.module")
}

dependencies {
    implementation(libs.managed.microstream.storage.restservice)

    testImplementation(project(":microstream"))
}

micronautBuild {
    binaryCompatibility {
        enabled.set(false)
    }
}
