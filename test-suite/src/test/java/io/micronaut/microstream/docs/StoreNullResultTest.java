package io.micronaut.microstream.docs;

import io.micronaut.context.ApplicationContext;
import io.micronaut.core.util.CollectionUtils;
import one.microstream.storage.embedded.types.EmbeddedStorageManager;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StoreNullResultTest {

    @Test
    void testParamsCanBeNull() {
        String storageDirectory = "build/microstream-" + UUID.randomUUID();
        Map<String, Object> properties = CollectionUtils.mapOf(
            "microstream.storage.main.storage-directory",
            storageDirectory,
            "microstream.storage.main.root-class",
            "io.micronaut.microstream.docs.Data");
        ApplicationContext ctx = ApplicationContext.run(properties);
        assertTrue(ctx.getBean(EmbeddedStorageManager.class).root() instanceof Data);
        Data data = (Data) ctx.getBean(EmbeddedStorageManager.class).root();
        assertTrue(data.getCustomers().isEmpty());

        StoreNullResult storeNullResult = ctx.getBean(StoreNullResult.class);

        storeNullResult.saveNullParams(new Customer(UUID.randomUUID().toString(), "Sergio", "del Amo"));

        ctx.close();

        ctx = ApplicationContext.run(properties);

        assertTrue(ctx.getBean(EmbeddedStorageManager.class).root() instanceof Data);
        data = (Data) ctx.getBean(EmbeddedStorageManager.class).root();
        assertFalse(data.getCustomers().isEmpty());

        ctx.close();
    }

    @Test
    void testResultCanBeNull() {
        String storageDirectory = "build/microstream-" + UUID.randomUUID();
        Map<String, Object> properties = CollectionUtils.mapOf(
            "microstream.storage.main.storage-directory",
            storageDirectory,
            "microstream.storage.main.root-class",
            "io.micronaut.microstream.docs.Data");
        ApplicationContext ctx = ApplicationContext.run(properties);
        assertTrue(ctx.getBean(EmbeddedStorageManager.class).root() instanceof Data);
        Data data = (Data) ctx.getBean(EmbeddedStorageManager.class).root();
        assertTrue(data.getCustomers().isEmpty());

        StoreNullResult storeNullResult = ctx.getBean(StoreNullResult.class);

        storeNullResult.saveNullResult(new Customer(UUID.randomUUID().toString(), "Sergio", "del Amo"));

        ctx.close();

        ctx = ApplicationContext.run(properties);

        assertTrue(ctx.getBean(EmbeddedStorageManager.class).root() instanceof Data);
        data = (Data) ctx.getBean(EmbeddedStorageManager.class).root();
        assertFalse(data.getCustomers().isEmpty());

        ctx.close();
    }

}
