package io.micronaut.microstream.docs;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import io.micronaut.core.annotation.NonNull;
import jakarta.inject.Singleton;
import one.microstream.concurrency.XThreads;
import one.microstream.storage.embedded.types.EmbeddedStorageManager;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Singleton
public class CustomerRepositoryImpl implements CustomerRepository {

    private final EmbeddedStorageManager embeddedStorageManager;

    public CustomerRepositoryImpl(EmbeddedStorageManager embeddedStorageManager) {
        this.embeddedStorageManager = embeddedStorageManager;
    }

	@Override
	public void save(@NonNull @NotNull @Valid Customer customer) {
        XThreads.executeSynchronized(() -> {
            getData().ifPresent(data -> data.add(customer));
            embeddedStorageManager.storeAll();
        });
	}

    @Override
    @NonNull
    public Optional<Customer> findById(@NonNull @NotBlank String id) {
        return getData().flatMap(data -> data.findById(id));
    }

    @Override
    public void deleteById(@NonNull @NotBlank String id) {
        XThreads.executeSynchronized(() -> {
            getData().ifPresent(data -> data.remove(id));
            embeddedStorageManager.storeAll();
        });
    }

    @Override
    @NonNull
	public Collection<Customer> findByFirstName(@NonNull @NotBlank String firstName) {
        return customersByFirstName(getData()
            .map(Data::getCustomers)
            .orElseGet(Collections::emptyList), firstName);
	}

    private static List<Customer> customersByFirstName(@NonNull Collection<Customer> customers,
                                                       @NonNull String firstName) {
        return customers.stream()
            .filter(c -> c.getFirstName().equals(firstName))
            .collect(Collectors.toList());
    }

    private Optional<Data> getData() {
        Object root = embeddedStorageManager.root();
        if (root instanceof Data) {
            return Optional.of((Data) root);
        }
        return Optional.empty();
    }
}
