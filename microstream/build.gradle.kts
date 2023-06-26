import io.micronaut.testresources.buildtools.KnownModules.JDBC_POSTGRESQL
import io.micronaut.testresources.buildtools.KnownModules.LOCALSTACK_S3

plugins {
    id("io.micronaut.internal.build.microstream-module")
    id("io.micronaut.test-resources") version "4.0.0-M6"
}

dependencies {
    compileOnly(mnMicrometer.micronaut.micrometer.core)
    compileOnly(projects.micronautMicrostreamAnnotations)
    compileOnly(libs.managed.microstream.aws.s3)
    compileOnly(libs.managed.microstream.aws.dynamodb)
    compileOnly(libs.managed.microstream.sql)
    compileOnly(mnAws.micronaut.aws.sdk.v2)
    compileOnly(mn.micronaut.management)
    compileOnly(mnSql.micronaut.jdbc)

    api(libs.managed.microstream.storage.embedded.configuration)

    implementation(mn.reactor)
    implementation(libs.microstream.persistence.binary.jdk8)
    implementation(libs.microstream.persistence.binary.jdk17)

    testImplementation(libs.testcontainers)

    testImplementation(mnSerde.micronaut.serde.jackson)
    testImplementation(mn.reactor.test)
    testImplementation(mnMicrometer.micronaut.micrometer.core)
    testImplementation(mn.micronaut.management)
    testImplementation(mn.micronaut.http.server.netty)
    testImplementation(mn.micronaut.http.client)
    testImplementation(projects.micronautMicrostreamAnnotations)

    testImplementation(projects.testSuiteUtils)

    // S3 connector tests
    testImplementation(libs.managed.microstream.aws.s3)
    testImplementation(mnAws.micronaut.aws.sdk.v2)

    // Postgres connector tests
    testImplementation(libs.managed.microstream.sql)
    testImplementation(mnSql.micronaut.jdbc.hikari)
    testRuntimeOnly(mnSql.postgresql)

    // DynamoDB tests
    testImplementation(libs.managed.microstream.aws.dynamodb)

    testRuntimeOnly(mnLogging.logback.classic)
}

micronaut {
    importMicronautPlatform.set(false)
    testResources {
        enabled.set(true)
        additionalModules.add(LOCALSTACK_S3)
        additionalModules.add(JDBC_POSTGRESQL)
    }
}
