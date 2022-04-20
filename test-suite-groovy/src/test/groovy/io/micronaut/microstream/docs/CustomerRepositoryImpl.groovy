package io.micronaut.microstream.docs

import io.micronaut.core.annotation.NonNull
import io.micronaut.microstream.annotation.StoreAll
import jakarta.inject.Singleton
import one.microstream.storage.embedded.types.EmbeddedStorageManager

import javax.validation.Valid
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import java.util.stream.Collectors

@Singleton
class CustomerRepositoryImpl implements CustomerRepository {

    private final EmbeddedStorageManager embeddedStorageManager

    CustomerRepositoryImpl(EmbeddedStorageManager embeddedStorageManager) { // <1>
        this.embeddedStorageManager = embeddedStorageManager
    }

	@Override
    @StoreAll
    void save(@NonNull @NotNull @Valid Customer customer) {
        data().ifPresent(d -> d.add(customer))
	}

    @Override
    @NonNull
    Optional<Customer> findById(@NonNull @NotBlank String id) {
        return data().flatMap(data -> data.findById(id))
    }

    @Override
    @StoreAll
    void deleteById(@NonNull @NotBlank String id) {
        data().ifPresent(d -> d.remove(id))
    }

    @Override
    @NonNull
    Collection<Customer> findByFirstName(@NonNull @NotBlank String firstName) {
        return customersByFirstName(data()
            .map(Data::getCustomers)
            .orElseGet(Collections::emptyList), firstName)
	}

    private static List<Customer> customersByFirstName(@NonNull Collection<Customer> customers,
                                                       @NonNull String firstName) {
        return customers.stream()
            .filter(c -> c.firstName == firstName)
            .collect(Collectors.toList())
    }

    private Optional<Data> data() {
        Object root = embeddedStorageManager.root()
        if (root instanceof Data) {
            return Optional.of((Data) root)
        }
        return Optional.empty()
    }
}
