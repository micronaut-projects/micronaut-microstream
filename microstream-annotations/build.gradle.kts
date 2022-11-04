plugins {
    id("io.micronaut.internal.build.microstream-module")
}
dependencies {
    implementation(mn.micronaut.aop)
    implementation(mn.micronaut.core.processor)
}
