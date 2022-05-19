plugins {
    id("io.micronaut.build.internal.module")
}

dependencies {
    api(libs.managed.microstream.persistence.binary.jdk8)
    implementation(libs.managed.microstream.storage.embedded.configuration)

    testImplementation(project(":microstream"))
    testImplementation(mn.micronaut.http.server.netty)
}

micronautBuild {
    binaryCompatibility {
        enabled.set(false)
    }
}
