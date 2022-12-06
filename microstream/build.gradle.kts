plugins {
    id("io.micronaut.internal.build.microstream-module")
}

dependencies {
    compileOnly(mnMicrometer.micronaut.micrometer.core)
    compileOnly(project(":microstream-annotations"))
    api(libs.managed.microstream.storage.embedded.configuration)

    compileOnly(mn.micronaut.management)
    implementation(libs.projectreactor)
    implementation(libs.microstream.persistence.binary.jdk8)
    implementation(libs.microstream.persistence.binary.jdk17)

    testImplementation(mnSerde.micronaut.serde.jackson)
    testImplementation(libs.projectreactor.test)
    testImplementation(mnMicrometer.micronaut.micrometer.core)
    testImplementation(mn.micronaut.management)
    testImplementation(mn.micronaut.http.server.netty)
    testImplementation(mn.micronaut.http.client)
    testImplementation(project(":microstream-annotations"))
}
