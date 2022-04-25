package io.micronaut.microstream.docs;

import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.NonNull;
import jakarta.inject.Singleton;
import one.microstream.concurrency.XThreads;
import one.microstream.storage.embedded.types.EmbeddedStorageManager;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Optional;
import java.util.UUID;

@Requires(property = "customer.repository", value = "embedded-storage-manager")
//tag::clazz[]
@Singleton
public class CustomerRepositoryImpl implements CustomerRepository {

    private final EmbeddedStorageManager embeddedStorageManager;

    public CustomerRepositoryImpl(EmbeddedStorageManager embeddedStorageManager) { // <1>
        this.embeddedStorageManager = embeddedStorageManager;
    }

	@Override
    @NonNull
	public Customer save(@NonNull @NotNull @Valid CustomerSave customerSave) {
        return XThreads.executeSynchronized(() -> { // <2>
            String id = UUID.randomUUID().toString();
            Customer customer = new Customer(id, customerSave.getFirstName(), customerSave.getLastName());
            data().getCustomers().put(id, customer);
            embeddedStorageManager.store(data().getCustomers()); // <3>
            return customer;
        });
	}

    @Override
    public void update(@NonNull @NotNull @Valid Customer customer) {
        XThreads.executeSynchronized(() -> { // <2>
            Customer c = data().getCustomers().get(customer.getId());
            c.setFirstName(customer.getFirstName());
            c.setLastName(customer.getLastName());
            embeddedStorageManager.store(c); // <3>
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
            embeddedStorageManager.store(data().getCustomers()); // <3>
        });
    }

    private Data data() {
        return (Data) embeddedStorageManager.root();
    }
}
//end::clazz[]
