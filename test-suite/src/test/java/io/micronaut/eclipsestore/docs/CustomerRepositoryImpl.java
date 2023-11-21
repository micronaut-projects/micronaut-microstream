package io.micronaut.eclipsestore.docs;

import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.NonNull;
import jakarta.inject.Singleton;
import org.eclipse.serializer.concurrency.XThreads;
import org.eclipse.store.storage.types.StorageManager;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Optional;
import java.util.UUID;

@Requires(property = "customer.repository", value = "embedded-storage-manager")
//tag::clazz[]
@Singleton
public class CustomerRepositoryImpl implements CustomerRepository {

    private final StorageManager storageManager;

    public CustomerRepositoryImpl(StorageManager storageManager) { // <1>
        this.storageManager = storageManager;
    }

	@Override
    @NonNull
	public Customer save(@NonNull @NotNull @Valid CustomerSave customerSave) {
        return XThreads.executeSynchronized(() -> { // <2>
            String id = UUID.randomUUID().toString();
            Customer customer = new Customer(id, customerSave.getFirstName(), customerSave.getLastName());
            data().getCustomers().put(id, customer);
            storageManager.store(data().getCustomers()); // <3>
            return customer;
        });
	}

    @Override
    public void update(@NonNull @NotBlank String id,
                       @NonNull @NotNull @Valid CustomerSave customerSave) {
        XThreads.executeSynchronized(() -> { // <2>
            Customer c = data().getCustomers().get(id);
            c.setFirstName(customerSave.getFirstName());
            c.setLastName(customerSave.getLastName());
            storageManager.store(c); // <3>
        });
    }

    @Override
    @NonNull
    public Optional<Customer> findById(@NonNull @NotBlank String id) {
        return Optional.ofNullable(data().getCustomers().get(id));
    }

    @Override
    public void deleteById(@NonNull @NotBlank String id) {
        XThreads.executeSynchronized(() -> { // <2>
            data().getCustomers().remove(id);
            storageManager.store(data().getCustomers()); // <3>
        });
    }

    private Data data() {
        return (Data) storageManager.root();
    }
}
//end::clazz[]
