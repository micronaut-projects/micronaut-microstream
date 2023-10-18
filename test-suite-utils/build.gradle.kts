plugins {
    id("java-library")
    id("io.micronaut.build.internal.common")
    id("io.micronaut.internal.build.microstream-base")
}

dependencies {
    implementation(libs.managed.microstream.aws.s3)
    implementation(libs.testcontainers)
}

spotless {
    java {
        targetExclude("**/testutils/**")
    }
}

tasks.withType<Checkstyle> {
    enabled = false
}
