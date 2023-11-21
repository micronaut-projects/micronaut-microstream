package io.micronaut.eclipsestore.docs;

import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.eclipsestore.annotations.Store;
import io.micronaut.eclipsestore.annotations.StoreRoot;
import io.micronaut.eclipsestore.annotations.StoringStrategy;
import jakarta.inject.Singleton;
import org.eclipse.store.storage.types.StorageManager;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Optional;
import java.util.UUID;

@Requires(property = "customer.repository", value = "store-root-eager")
//tag::clazz[]
@Singleton
public class CustomerRepositoryStoreRootEagerImpl implements CustomerRepository {

    private final StorageManager storageManager;

    public CustomerRepositoryStoreRootEagerImpl(StorageManager storageManager) { // <1>
        this.storageManager = storageManager;
    }

	@Override
    @NonNull
    @StoreRoot(strategy = StoringStrategy.EAGER)
	public Customer save(@NonNull @NotNull @Valid CustomerSave customerSave) {
        String id = UUID.randomUUID().toString();
        Customer customer = new Customer(id, customerSave.getFirstName(), customerSave.getLastName());
        data().getCustomers().put(id, customer);
        return customer;
	}

    @Override
    @StoreRoot(strategy = StoringStrategy.EAGER)
    public void update(@NonNull @NotBlank String id,
                       @NonNull @NotNull @Valid CustomerSave customerSave) {
        Customer c = data().getCustomers().get(id);
        c.setFirstName(customerSave.getFirstName());
        c.setLastName(customerSave.getLastName());
    }

    @Override
    @NonNull
    public Optional<Customer> findById(@NonNull @NotBlank String id) {
        return Optional.ofNullable(data().getCustomers().get(id));
    }

    @Override
    @Store(root = true, strategy = StoringStrategy.EAGER)
    public void deleteById(@NonNull @NotBlank String id) {
        data().getCustomers().remove(id);
    }

    private Data data() {
        return (Data) storageManager.root();
    }
}
//end::clazz[]
