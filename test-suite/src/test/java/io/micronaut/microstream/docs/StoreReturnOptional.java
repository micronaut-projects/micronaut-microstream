package io.micronaut.microstream.docs;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.microstream.annotation.Store;
import jakarta.inject.Singleton;
import one.microstream.concurrency.XThreads;
import one.microstream.storage.embedded.types.EmbeddedStorageManager;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Optional;

@Singleton
public class StoreReturnOptional {
    private final EmbeddedStorageManager embeddedStorageManager;

    public StoreReturnOptional(EmbeddedStorageManager embeddedStorageManager) {
        this.embeddedStorageManager = embeddedStorageManager;
    }

    public void save(@NonNull @NotNull @Valid Customer customer) {
        XThreads.executeSynchronized(() -> {
            data().getCustomers().put(customer.getId(), customer);
            embeddedStorageManager.store(data().getCustomers());
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
        return (Data) embeddedStorageManager.root();
    }
}
