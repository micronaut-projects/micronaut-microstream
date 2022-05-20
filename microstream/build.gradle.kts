plugins {
    id("io.micronaut.build.internal.module")
}

dependencies {
    compileOnly(mn.micronaut.micrometer.core)
    compileOnly(project(":microstream-annotations"))
    api(libs.managed.microstream.storage.embedded.configuration)

    compileOnly(mn.micronaut.management)
    implementation(libs.projectreactor)

    testImplementation(libs.projectreactor.test)
    testImplementation(mn.micronaut.micrometer.core)
    testImplementation(mn.micronaut.management)
    testImplementation(mn.micronaut.http.server.netty)
    testImplementation(mn.micronaut.http.client)
    testImplementation(project(":microstream-annotations"))
}
