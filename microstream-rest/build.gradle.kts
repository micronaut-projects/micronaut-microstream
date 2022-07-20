plugins {
    id("io.micronaut.build.internal.module")
}

dependencies {
    annotationProcessor(mn.micronaut.validation)
    annotationProcessor(mn.micronaut.serde.processor)

    implementation(libs.managed.microstream.storage.restservice)
    implementation(mn.micronaut.validation)

    implementation(project(":microstream"))
    implementation(mn.micronaut.serde.jackson)

    testImplementation(mn.micronaut.http.server.netty)
    testImplementation(mn.micronaut.http.client)
    testImplementation(libs.logback.classic)
}

configurations.all {
    resolutionStrategy.dependencySubstitution {
        substitute(module("io.micronaut:micronaut-jackson-databind"))
            .using(module("io.micronaut.serde:micronaut-serde-jackson:${mn.versions.micronaut.serialization.get()}"))
    }
}

micronautBuild {
    binaryCompatibility {
        enabled.set(false)
    }
}
