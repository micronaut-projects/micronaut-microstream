plugins {
    id("io.micronaut.build.internal.module")
}

dependencies {
    annotationProcessor(libs.micronaut.serde.processor)

    api(libs.micronaut.serde.api)

    implementation(libs.managed.microstream.storage.restservice)
    implementation(libs.micronaut.validation)

    testImplementation(project(":microstream"))
}

configurations.all {
    resolutionStrategy.dependencySubstitution {
        substitute(module("io.micronaut:micronaut-jackson-databind"))
            .using(module("io.micronaut.serde:micronaut-serde-jackson:1.0.1"))
    }
}

micronautBuild {
    binaryCompatibility {
        enabled.set(false)
    }
}
