package io.micronaut.eclipsestore.docs;

import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.eclipsestore.annotations.StoreReturn;
import io.micronaut.eclipsestore.annotations.StoringStrategy;
import jakarta.inject.Singleton;
import org.eclipse.store.storage.types.StorageManager;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

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
