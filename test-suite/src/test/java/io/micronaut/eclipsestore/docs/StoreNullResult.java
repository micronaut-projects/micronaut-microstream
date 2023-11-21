package io.micronaut.eclipsestore.docs;

import io.micronaut.eclipsestore.annotations.Store;
import jakarta.inject.Singleton;
import org.eclipse.store.storage.types.StorageManager;

import java.util.Map;

@Singleton
public class StoreNullResult {
    private final StorageManager storageManager;

    public StoreNullResult(StorageManager storageManager) {
        this.storageManager = storageManager;
    }

    public void saveNullResult(Customer customer) {
        saveNullResult(data().getCustomers(), customer);
    }

    @Store(result = true, parameters = {"customers"})
    Map<String, Customer> saveNullResult(Map<String, Customer> customers, Customer customer) {
        customers.put(customer.getId(), customer);
        return null;
    }

    public void saveNullParams(Customer customer) {
        saveNullParams(data().getCustomers(), customer, null);
    }

    @Store(result = true, parameters = {"secondParams"})
    Map<String, Customer> saveNullParams(Map<String, Customer> customers, Customer customer, Map<String, Object> secondParams) {
        customers.put(customer.getId(), customer);
        return customers;
    }


    Data data() {
        return (Data) storageManager.root();
    }
}
