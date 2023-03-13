package io.micronaut.microstream.docs;

import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.microstream.annotations.Store;
import io.micronaut.microstream.annotations.StoringStrategy;
import jakarta.inject.Singleton;
import one.microstream.storage.types.StorageManager;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@Requires(property = "spec.service", value = "store")
@Singleton
public class CrmCustomerServiceImpl implements CrmCustomerService {

    private final StorageManager storageManager;

    public CrmCustomerServiceImpl(StorageManager storageManager) {
        this.storageManager = storageManager;
    }

    @Override
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
