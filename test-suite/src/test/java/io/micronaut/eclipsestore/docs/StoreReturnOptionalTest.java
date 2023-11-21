package io.micronaut.eclipsestore.docs;

import io.micronaut.context.ApplicationContext;
import io.micronaut.core.util.CollectionUtils;
import org.eclipse.store.storage.types.StorageManager;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

class StoreReturnOptionalTest {

    @Test
    void testResultCanBeOptional() {
        String storageDirectory = "build/eclipsestore-" + UUID.randomUUID();
        Map<String, Object> properties = CollectionUtils.mapOf(
            "eclipsestore.storage.main.storage-directory", storageDirectory,
            "eclipsestore.storage.main.root-class", "io.micronaut.eclipsestore.docs.Data");
        ApplicationContext ctx = ApplicationContext.run(properties);
        assertTrue(ctx.getBean(StorageManager.class).root() instanceof Data);
        Data data = (Data) ctx.getBean(StorageManager.class).root();

        // When you start there is no data
        assertTrue(data.getCustomers().isEmpty());

        // Saving one customer
        String firstName = "Sergio";
        String lastName = "del Amo";
        String id = UUID.randomUUID().toString();
        Customer customer = new Customer(id, firstName, null);
        ctx.getBean(StoreReturnOptional.class).save(customer);

        ctx.close();

        ctx = ApplicationContext.run(properties);

        // The customer is there after stopping and starting
        assertTrue(ctx.getBean(StorageManager.class).root() instanceof Data);
        data = (Data) ctx.getBean(StorageManager.class).root();
        assertFalse(data.getCustomers().isEmpty());
        Optional<Customer> savedCustomerOptional = data.getCustomers().values().stream().findFirst();
        assertTrue(savedCustomerOptional.isPresent());

        Customer savedCustomer = savedCustomerOptional.get();
        assertEquals(id, savedCustomer.getId());
        assertEquals(firstName, savedCustomer.getFirstName());
        assertNull(savedCustomer.getLastName());

        // Update the customer in a method which returns an optional wrapping the updated customer
        ctx.getBean(StoreReturnOptional.class).updateCustomer(id, new CustomerSave(firstName, lastName));

        ctx.close();

        ctx = ApplicationContext.run(properties);

        // The Updated customer was saved
        savedCustomerOptional = data.getCustomers().values().stream().findFirst();
        assertTrue(savedCustomerOptional.isPresent());

        savedCustomer = savedCustomerOptional.get();
        assertEquals(id, savedCustomer.getId());
        assertEquals(firstName, savedCustomer.getFirstName());
        assertEquals(lastName, savedCustomer.getLastName());

        ctx.close();
    }
}
