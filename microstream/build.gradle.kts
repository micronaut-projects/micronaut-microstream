plugins {
    id("io.micronaut.build.internal.module")
}

dependencies {
    compileOnly(libs.micronaut.micrometer.core)
    api(libs.managed.microstream.storage.embedded.configuration)

    implementation(libs.micronaut.aop)

    testImplementation(libs.micronaut.micrometer.core)
    testImplementation(libs.micronaut.management)
    testImplementation(libs.micronaut.http.server.netty)
    testImplementation(libs.micronaut.http.client)
    testImplementation(libs.groovy.json)

}

micronautBuild {
    binaryCompatibility {
        enabled.set(false)
    }
}
