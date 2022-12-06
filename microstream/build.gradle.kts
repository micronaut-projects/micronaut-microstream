plugins {
    id("io.micronaut.internal.build.microstream-module")
}

dependencies {
    compileOnly(mnMicrometer.micronaut.micrometer.core)
    compileOnly(project(":microstream-annotations"))
    api(libs.managed.microstream.storage.embedded.configuration)

    compileOnly(mn.micronaut.management)
    implementation(mn.reactor)
    implementation(libs.microstream.persistence.binary.jdk8)
    implementation(libs.microstream.persistence.binary.jdk17)

    testImplementation(platform("io.projectreactor:reactor-bom:${mnReactor.versions.reactor.bom.get()}"))
    testImplementation(mnSerde.micronaut.serde.jackson)
    testImplementation(libs.reactor.test)
    testImplementation(mnMicrometer.micronaut.micrometer.core)
    testImplementation(mn.micronaut.management)
    testImplementation(mn.micronaut.http.server.netty)
    testImplementation(mn.micronaut.http.client)
    testImplementation(project(":microstream-annotations"))
}
