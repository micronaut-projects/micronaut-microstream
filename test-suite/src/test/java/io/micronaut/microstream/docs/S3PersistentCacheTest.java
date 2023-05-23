package io.micronaut.microstream.docs;

import io.micronaut.context.ApplicationContext;
import io.micronaut.context.annotation.ConfigurationBuilder;
import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.util.StringUtils;
import io.micronaut.runtime.server.EmbeddedServer;
import jakarta.inject.Singleton;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class S3PersistentCacheTest {

    private static final String BUCKET_NAME = "microstreamcache";

    @Test
    void cachePersistsOverRestarts() {
        Map<String, Object> config = Map.of(
            "s3.test", StringUtils.TRUE,
            "micronaut.metrics.enabled", StringUtils.FALSE,
            "aws.test-bucket-name", BUCKET_NAME,
            "microstream.cache.counter.key-type", "java.lang.String",
            "microstream.cache.counter.value-type", "java.lang.Long",
            "microstream.cache.counter.storage", "cache"
        );

        // When we create the app, and use a cached method
        try (EmbeddedServer server = ApplicationContext.run(EmbeddedServer.class, config)) {
            CounterService counter = server.getApplicationContext().getBean(CounterService.class);
            counter.setCount("Tim", 1337L);
            Long count = counter.currentCount("Tim");
            assertEquals(1337L, count);
            counter.setCount("Tim", 666L);
        }

        // Then restarting the app with the same storage location, the value is still cached
        try (EmbeddedServer server = ApplicationContext.run(EmbeddedServer.class, config)) {
            CounterService counter = server.getApplicationContext().getBean(CounterService.class);
            Long count = counter.currentCount("Tim");
            assertEquals(666L, count);
        }
    }
}
