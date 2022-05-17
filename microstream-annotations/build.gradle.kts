plugins {
    id("io.micronaut.build.internal.module")
}

dependencies {
    implementation(mn.micronaut.aop)
}

micronautBuild {
    binaryCompatibility {
        enabled.set(false)
    }
}
