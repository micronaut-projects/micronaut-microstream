package io.micronaut.microstream.docs;

import io.micronaut.context.ApplicationContext;
import io.micronaut.core.util.StringUtils;
import io.micronaut.runtime.server.EmbeddedServer;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PostgresPersistentCacheTest {

    @Test
    void cachePersistsOverRestarts() {
        Map<String, Object> config = Map.of(
            "datasources.cache.db-type", "postgresql",
            "micronaut.metrics.enabled", StringUtils.FALSE,
            "microstream.postgres.storage.cache.table-name", "microstream",
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
