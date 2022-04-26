package io.micronaut.microstream.docs;

import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.microstream.annotations.Store;
import io.micronaut.microstream.annotations.StoreReturn;
import io.micronaut.microstream.annotations.StoringStrategy;
import jakarta.inject.Singleton;
import one.microstream.storage.types.StorageManager;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Requires(property = "spec.service", value = "store-return")
@Singleton
public class CrmCustomerServiceStoreReturnImpl implements CrmCustomerService {

    private final StorageManager storageManager;

    public CrmCustomerServiceStoreReturnImpl(StorageManager storageManager) {
        this.storageManager = storageManager;
    }

    @Override
    @StoreReturn(strategy = StoringStrategy.EAGER)
    @NonNull
    public Customers save(@NonNull @NotNull @Valid Customer customer) {
        data().getCustomers().getCustomersById().put(customer.getId(), customer);
        return data().getCustomers();
    }

    private CRM data() {
        return (CRM) storageManager.root();
    }
}
