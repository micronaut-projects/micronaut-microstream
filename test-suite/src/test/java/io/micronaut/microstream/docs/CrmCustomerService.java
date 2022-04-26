package io.micronaut.microstream.docs;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.microstream.annotation.Store;
import io.micronaut.microstream.annotation.StoringStrategy;
import jakarta.inject.Singleton;
import one.microstream.storage.types.StorageManager;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Singleton
public class CrmCustomerService {

    private final StorageManager storageManager;

    public CrmCustomerService(StorageManager storageManager) {
        this.storageManager = storageManager;
    }

    @Store(result = true, strategy = StoringStrategy.EAGER)
    @NonNull
    public Customers save(@NonNull @NotNull @Valid Customer customer) {
        data().getCustomers().getCustomersById().put(customer.getId(), customer);
        return data().getCustomers();
    }


    private CRM data() {
        return (CRM) storageManager.root();
    }
}
