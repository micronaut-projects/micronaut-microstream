plugins {
    id("io.micronaut.internal.build.microstream-module")
}

dependencies {
    api(projects.micronautMicrostream)
    api(libs.managed.microstream.aws.s3)
    implementation(mnAws.micronaut.aws.sdk.v2)
    testImplementation(libs.jupiter.api)
}
