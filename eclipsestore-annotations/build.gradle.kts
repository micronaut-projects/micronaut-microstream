plugins {
    id("io.micronaut.internal.build.eclipsestore-module")
}
dependencies {
    implementation(mn.micronaut.aop)
    implementation(mn.micronaut.core.processor)
}
