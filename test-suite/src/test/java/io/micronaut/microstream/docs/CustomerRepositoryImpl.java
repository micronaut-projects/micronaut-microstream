package io.micronaut.microstream.docs;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.microstream.annotation.StoreReturn;
import jakarta.inject.Singleton;
import one.microstream.storage.embedded.types.EmbeddedStorageManager;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Singleton
public class CustomerRepositoryImpl implements CustomerRepository {

    private final EmbeddedStorageManager embeddedStorageManager;

    public CustomerRepositoryImpl(EmbeddedStorageManager embeddedStorageManager) {
        this.embeddedStorageManager = embeddedStorageManager;
    }

	@Override
    @StoreReturn
	public Map<String, Customer> save(@NonNull @NotNull @Valid Customer customer) {
        Data data = getData().get();
        data.add(customer);
        return data.getCustomerModel();
	}

    @Override
    @NonNull
    public Optional<Customer> findById(@NonNull @NotBlank String id) {
        return getData().flatMap(data -> data.findById(id));
    }

    @Override
    @StoreReturn
    public Map<String, Customer> deleteById(@NonNull @NotBlank String id) {
        Data data = getData().get();
        data.remove(id);
        return data.getCustomerModel();
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
