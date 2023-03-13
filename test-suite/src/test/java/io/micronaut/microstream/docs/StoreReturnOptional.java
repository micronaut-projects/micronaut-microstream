package io.micronaut.microstream.docs;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.microstream.annotations.Store;
import jakarta.inject.Singleton;
import one.microstream.concurrency.XThreads;
import one.microstream.storage.types.StorageManager;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.Optional;

@Singleton
public class StoreReturnOptional {
    private final StorageManager storageManager;

    public StoreReturnOptional(StorageManager storageManager) {
        this.storageManager = storageManager;
    }

    public void save(@NonNull @NotNull @Valid Customer customer) {
        XThreads.executeSynchronized(() -> {
            data().getCustomers().put(customer.getId(), customer);
            storageManager.store(data().getCustomers());
        });
    }

    @Store(result = true)
    @Nullable
    protected Optional<Customer> updateCustomer(@NonNull String id,
                                                @NonNull CustomerSave customerSave) {
        Customer c = data().getCustomers().get(id);
        if (c != null) {
            c.setFirstName(customerSave.getFirstName());
            c.setLastName(customerSave.getLastName());
            return Optional.of(c);
        }
        return Optional.empty();
    }

    Data data() {
        return (Data) storageManager.root();
    }
}
