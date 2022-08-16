plugins {
    id("io.micronaut.build.internal.module")
}

repositories {
    maven { setUrl("https://s01.oss.sonatype.org/content/repositories/snapshots/") }
    mavenCentral()
}

dependencies {
    implementation(mn.micronaut.aop)
}
