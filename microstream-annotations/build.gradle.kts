plugins {
    id("io.micronaut.build.internal.module")
}

dependencies {
    implementation(libs.micronaut.aop)
}

micronautBuild {
    binaryCompatibility {
        enabled.set(false)
    }
}
