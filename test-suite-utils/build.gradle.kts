plugins {
    id("java-library")
    id("io.micronaut.build.internal.common")
    id("io.micronaut.internal.build.eclipsestore-base")
}

dependencies {
    implementation(libs.managed.eclipsestore.aws.s3)
    implementation(platform(mnAws.micronaut.aws.bom))
    implementation(libs.awssdk.s3)
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
