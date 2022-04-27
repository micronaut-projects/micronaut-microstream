plugins {
    id("io.micronaut.build.internal.module")
}

dependencies {
    annotationProcessor(libs.micronaut.serde.processor)
    annotationProcessor(libs.micronaut.validation)

    api(libs.micronaut.serde.api)

    implementation(libs.managed.microstream.storage.restservice)
    implementation(libs.micronaut.validation)
    implementation(libs.micronaut.router)
    implementation(project(":microstream"))
    implementation(libs.micronaut.serde.jackson)
    testImplementation(libs.micronaut.http.server.netty)
    testImplementation(libs.micronaut.http.client)
    testImplementation(libs.micronaut.management)
}

configurations.all {
    resolutionStrategy.dependencySubstitution {
        substitute(module("io.micronaut:micronaut-jackson-databind"))
            .using(module("io.micronaut.serde:micronaut-serde-jackson:1.0.0"))
    }
}

micronautBuild {
    binaryCompatibility {
        enabled.set(false)
    }
}
