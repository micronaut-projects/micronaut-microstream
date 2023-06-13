plugins {
    id("java-library")
    id("io.micronaut.internal.build.microstream-base")
}
dependencies {
    implementation(libs.managed.microstream.aws.s3)
    implementation(libs.testcontainers)
}
