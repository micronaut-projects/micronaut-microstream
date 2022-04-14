package io.micronaut.microstream.docs

import io.micronaut.core.annotation.NonNull
import jakarta.inject.Singleton
import one.microstream.concurrency.XThreads
import one.microstream.storage.embedded.types.EmbeddedStorageManager

import javax.validation.Valid
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import java.util.stream.Collectors

@Singleton
class CustomerRepositoryImpl implements CustomerRepository {

    private final EmbeddedStorageManager embeddedStorageManager

    CustomerRepositoryImpl(EmbeddedStorageManager embeddedStorageManager) {
        this.embeddedStorageManager = embeddedStorageManager
    }

	@Override
    void save(@NonNull @NotNull @Valid Customer customer) {
        XThreads.executeSynchronized(new Runnable() {
            @Override
            void run() {
                data().ifPresent(d -> d.add(customer))
                storeAll()
            }
        })
	}

    @Override
    @NonNull
    Optional<Customer> findById(@NonNull @NotBlank String id) {
        return data().flatMap(data -> data.findById(id))
    }

    @Override
    void deleteById(@NonNull @NotBlank String id) {
        XThreads.executeSynchronized(new Runnable() {
            @Override
            void run() {
                data().ifPresent(d -> d.remove(id))
                storeAll()
            }
        })
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

    private void storeAll() {
        embeddedStorageManager.storeAll()
    }

    private Optional<Data> data() {
        Object root = embeddedStorageManager.root()
        if (root instanceof Data) {
            return Optional.of((Data) root)
        }
        return Optional.empty()
    }
}
