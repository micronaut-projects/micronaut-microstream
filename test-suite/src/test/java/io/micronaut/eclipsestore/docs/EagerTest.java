package io.micronaut.eclipsestore.docs;

import io.micronaut.context.ApplicationContext;
import io.micronaut.core.util.CollectionUtils;
import org.eclipse.store.storage.types.StorageManager;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EagerTest {

    @ParameterizedTest
    @ValueSource(strings = {
        "store",
        "store-return"
    })
    void testEagerStrategyWithReturn(String serviceImplementation) {
        String storageDirectory = "build/eclipsestore-" + UUID.randomUUID();
        Map<String, Object> properties = CollectionUtils.mapOf(
            "eclipsestore.storage.main.storage-directory", storageDirectory,
            "eclipsestore.storage.main.root-class", "io.micronaut.eclipsestore.docs.CRM",
            "spec.service", serviceImplementation);
        ApplicationContext ctx = ApplicationContext.run(properties);
        CRM data = (CRM) ctx.getBean(StorageManager.class).root();
        assertTrue(data.getCustomers().getCustomersById().isEmpty());
        ctx.getBean(CrmCustomerService.class).save(new Customer(UUID.randomUUID().toString(), "Sergio", "del Amo"));
        ctx.close();
        ctx = ApplicationContext.run(properties);
        data = (CRM) ctx.getBean(StorageManager.class).root();
        assertFalse(data.getCustomers().getCustomersById().isEmpty());
        ctx.close();
    }
}
