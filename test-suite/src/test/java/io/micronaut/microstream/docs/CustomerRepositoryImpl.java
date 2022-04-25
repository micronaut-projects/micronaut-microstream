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

@Requires(property = "customer.repository", value = "embedded-storage-manager")
//tag::clazz[]
@Singleton
public class CustomerRepositoryImpl implements CustomerRepository {

    private final EmbeddedStorageManager embeddedStorageManager;

    public CustomerRepositoryImpl(EmbeddedStorageManager embeddedStorageManager) { // <1>
        this.embeddedStorageManager = embeddedStorageManager;
    }

	@Override
	public void save(@NonNull @NotNull @Valid Customer customer) {
        XThreads.executeSynchronized(() -> {
            getData().ifPresent(data -> {
                data.getCustomers().put(customer.getId(), customer);
                embeddedStorageManager.store(data.getCustomers()); // <2>
            });
        });
	}

    @Override
    @NonNull
    public Optional<Customer> findById(@NonNull @NotBlank String id) {
        return getData().flatMap(data -> Optional.ofNullable(data.getCustomers().get(id)));
    }

    @Override
    public void deleteById(@NonNull @NotBlank String id) {
        XThreads.executeSynchronized(() -> {
            getData().ifPresent(data -> {
                data.getCustomers().remove(id);
                embeddedStorageManager.store(data.getCustomers()); // <2>
            });
        });
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
