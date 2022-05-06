package io.micronaut.microstream.docs;

import io.micronaut.context.ApplicationContext;
import io.micronaut.core.util.CollectionUtils;
import io.micronaut.runtime.server.EmbeddedServer;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CacheTest {

    @Test
    void cacheWorksAsExpected() {
        Map<String, Object> config = CollectionUtils.mapOf(
            "storageDirectory", "build/microstream-cache-" + UUID.randomUUID()
        );
        // When we create the app, and use a cached method
        try (EmbeddedServer server = ApplicationContext.run(EmbeddedServer.class, config, "cache")) {
            CounterService counter = server.getApplicationContext().getBean(CounterService.class);
            counter.setCount("Tim", 1337L);
            Long count = counter.currentCount("Tim");
            assertEquals(1337L, count);

            // Change the store so we check we're seeing a cached value
            counter.counters.put("Tim", -1L);
            count = counter.currentCount("Tim");
            assertEquals(1337L, count);
        }
    }
}
