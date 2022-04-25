package io.micronaut.microstream.docs

import io.micronaut.context.annotation.Requires
import io.micronaut.core.annotation.NonNull
import jakarta.inject.Singleton
import one.microstream.concurrency.XThreads
import one.microstream.storage.embedded.types.EmbeddedStorageManager
import javax.validation.Valid
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Requires(property = "customer.repository", value = "embedded-storage-manager")
//tag::clazz[]
@Singleton
class CustomerRepositoryImpl implements CustomerRepository {

    private final EmbeddedStorageManager embeddedStorageManager

    CustomerRepositoryImpl(EmbeddedStorageManager embeddedStorageManager) { // <1>
        this.embeddedStorageManager = embeddedStorageManager
    }

	@Override
    void save(@NonNull @NotNull @Valid Customer customer) {
        XThreads.executeSynchronized(new Runnable() {
            @Override
            void run() {
                data().ifPresent(d -> {
                    d.customers[customer.id] = customer
                    store(d.customers) // <2>
                })
            }
        })
	}

    @Override
    @NonNull
    Optional<Customer> findById(@NonNull @NotBlank String id) {
        data().flatMap(d -> Optional.ofNullable(d.customers[id]))
    }

    @Override
    void deleteById(@NonNull @NotBlank String id) {
        XThreads.executeSynchronized(new Runnable() {
            @Override
            void run() {
                data().ifPresent(d -> {
                    d.customers.remove(id)
                    store(d.customers) // <2>
                })
            }
        })
    }

    private void store(Object instance) {
        embeddedStorageManager.store(instance)
    }

    private Optional<Data> data() {
        Object root = embeddedStorageManager.root()
        (root instanceof Data) ? Optional.of((Data) root) : (Optional.empty() as Optional<Data>)
    }
}
//end::clazz[]
