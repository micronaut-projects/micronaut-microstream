package io.micronaut.microstream.docs;

import io.micronaut.context.ApplicationContext;
import io.micronaut.context.BeanContext;
import io.micronaut.inject.qualifiers.Qualifiers;
import io.micronaut.runtime.server.EmbeddedServer;
import jakarta.inject.Inject;
import one.microstream.storage.restservice.sparkjava.types.StorageRestServiceSparkJava;
import one.microstream.storage.types.StorageManager;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import spark.Service;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Disabled
class PersistentCacheTest {

    @Inject
    BeanContext beanContext;

    @Test
    void cacheIsCreated() {
        try (EmbeddedServer server = ApplicationContext.run(EmbeddedServer.class, "cache")) {
            CounterService counter = server.getApplicationContext().getBean(CounterService.class);
            counter.setCount("Tim", 1337L);
            Long count = counter.currentCount("Tim");
            assertEquals(1337L, count);
            StorageManager storage = server.getApplicationContext().getBean(StorageManager.class, Qualifiers.byName("backing"));
            StorageRestServiceSparkJava service = StorageRestServiceSparkJava.New(storage);
            service.setSparkService(
                Service.ignite().port(4567)
            );
            service.start();
            assertEquals(1, 1);
            service.stop();
        }
        try (EmbeddedServer server = ApplicationContext.run(EmbeddedServer.class, "cache")) {
            StorageManager storage = server.getApplicationContext().getBean(StorageManager.class, Qualifiers.byName("backing"));
            StorageRestServiceSparkJava service = StorageRestServiceSparkJava.New(storage);
            service.setSparkService(
                Service.ignite().port(4567)
            );
            service.start();
            CounterService counter = server.getApplicationContext().getBean(CounterService.class);
            Long count = counter.currentCount("Tim");
            assertEquals(1337L, count);
            service.stop();
        }
    }
}
