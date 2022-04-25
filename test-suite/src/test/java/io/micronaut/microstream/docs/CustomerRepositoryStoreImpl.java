package io.micronaut.microstream.docs;

import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.microstream.annotation.Store;
import jakarta.inject.Singleton;
import one.microstream.storage.embedded.types.EmbeddedStorageManager;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Map;
import java.util.Optional;

@Requires(property = "customer.repository", value = "store")
//tag::clazz[]
@Singleton
public class CustomerRepositoryStoreImpl implements CustomerRepository {

    private final EmbeddedStorageManager embeddedStorageManager;

    public CustomerRepositoryStoreImpl(EmbeddedStorageManager embeddedStorageManager) { // <1>
        this.embeddedStorageManager = embeddedStorageManager;
    }

	@Override
	public void save(@NonNull @NotNull @Valid Customer customer) {
        getData().ifPresent(data -> {
            addCustomer(data.getCustomers(), customer);
        });
	}

	@Store(parameters = "customers") // <2>
	protected void addCustomer(@NonNull Map<String, Customer> customers,
                               @NonNull Customer customer) {
        customers.put(customer.getId(), customer);
    }

    @Override
    @NonNull
    public Optional<Customer> findById(@NonNull @NotBlank String id) {
        return getData().flatMap(data -> Optional.ofNullable(data.getCustomers().get(id)));
    }

    @Override
    public void deleteById(@NonNull @NotBlank String id) {
      getData().ifPresent(data -> {
          removeCustomer(data.getCustomers(), id);
        });
    }

    @Store(parameters = "customers") // <2>
    protected void removeCustomer(@NonNull Map<String, Customer> customers,
                                  @NonNull String id) {
        customers.remove(id);
    }

    private Optional<Data> getData() {
        Object root = embeddedStorageManager.root();
        if (root instanceof Data) {
            return Optional.of((Data) root);
        }
        return Optional.empty();
    }
}
//end::clazz[]
