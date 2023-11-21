package io.micronaut.eclipsestore.docs;

import io.micronaut.context.ApplicationContext;
import io.micronaut.core.util.CollectionUtils;
import org.eclipse.store.storage.types.StorageManager;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StoreNullResultTest {

    @Test
    void testParamsCanBeNull() {
        String storageDirectory = "build/eclipsestore-" + UUID.randomUUID();
        Map<String, Object> properties = CollectionUtils.mapOf(
            "eclipsestore.storage.main.storage-directory",
            storageDirectory,
            "eclipsestore.storage.main.root-class",
            "io.micronaut.eclipsestore.docs.Data");
        ApplicationContext ctx = ApplicationContext.run(properties);
        assertTrue(ctx.getBean(StorageManager.class).root() instanceof Data);
        Data data = (Data) ctx.getBean(StorageManager.class).root();
        assertTrue(data.getCustomers().isEmpty());

        StoreNullResult storeNullResult = ctx.getBean(StoreNullResult.class);

        storeNullResult.saveNullParams(new Customer(UUID.randomUUID().toString(), "Sergio", "del Amo"));

        ctx.close();

        ctx = ApplicationContext.run(properties);

        assertTrue(ctx.getBean(StorageManager.class).root() instanceof Data);
        data = (Data) ctx.getBean(StorageManager.class).root();
        assertFalse(data.getCustomers().isEmpty());

        ctx.close();
    }

    @Test
    void testResultCanBeNull() {
        String storageDirectory = "build/eclipsestore-" + UUID.randomUUID();
        Map<String, Object> properties = CollectionUtils.mapOf(
            "eclipsestore.storage.main.storage-directory",
            storageDirectory,
            "eclipsestore.storage.main.root-class",
            "io.micronaut.eclipsestore.docs.Data");
        ApplicationContext ctx = ApplicationContext.run(properties);
        assertTrue(ctx.getBean(StorageManager.class).root() instanceof Data);
        Data data = (Data) ctx.getBean(StorageManager.class).root();
        assertTrue(data.getCustomers().isEmpty());

        StoreNullResult storeNullResult = ctx.getBean(StoreNullResult.class);

        storeNullResult.saveNullResult(new Customer(UUID.randomUUID().toString(), "Sergio", "del Amo"));

        ctx.close();

        ctx = ApplicationContext.run(properties);

        assertTrue(ctx.getBean(StorageManager.class).root() instanceof Data);
        data = (Data) ctx.getBean(StorageManager.class).root();
        assertFalse(data.getCustomers().isEmpty());

        ctx.close();
    }

}
