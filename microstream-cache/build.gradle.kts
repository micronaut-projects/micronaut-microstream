plugins {
    id("io.micronaut.build.internal.module")
}

repositories {
    maven { setUrl("https://s01.oss.sonatype.org/content/repositories/snapshots/") }
    mavenCentral()
}

dependencies {
    api(project(":microstream"))
    api(libs.managed.microstream.cache)
    api(mn.micronaut.cache.core)

    testImplementation(mn.micronaut.cache.tck)
    testImplementation(libs.jupiter.api)
}
