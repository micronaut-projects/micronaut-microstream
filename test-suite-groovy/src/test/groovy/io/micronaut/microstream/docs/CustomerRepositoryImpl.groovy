package io.micronaut.microstream.docs

import io.micronaut.context.annotation.Requires
import io.micronaut.core.annotation.NonNull
import jakarta.inject.Singleton
import one.microstream.concurrency.XThreads
import one.microstream.storage.types.StorageManager

import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.util.function.Supplier

@Requires(property = "customer.repository", value = "embedded-storage-manager")
//tag::clazz[]
@Singleton
class CustomerRepositoryImpl implements CustomerRepository {

    private final StorageManager storageManager

    CustomerRepositoryImpl(StorageManager storageManager) { // <1>
        this.storageManager = storageManager
    }

    @Override
    @NonNull
    Customer save(@NonNull @NotNull @Valid CustomerSave customerSave) {
        XThreads.executeSynchronized(new Supplier<Customer>() { // <2>
            @Override
            Customer get() {
                String id = UUID.randomUUID().toString()
                Customer customer = new Customer(id, customerSave.getFirstName(), customerSave.getLastName())
                data().getCustomers().put(id, customer)
                store(data().getCustomers()) // <3>
                customer
            }
        })
    }

    @Override
    void update(@NonNull @NotBlank String id,
                @NonNull @NotNull @Valid CustomerSave customerSave) {
        XThreads.executeSynchronized(new Runnable() { // <2>
            @Override
            void run() {
                Customer c = data().getCustomers().get(id)
                c.setFirstName(customerSave.getFirstName())
                c.setLastName(customerSave.getLastName())
                store(c) // <3>
            }
        })
    }

    @Override
    @NonNull
    Optional<Customer> findById(@NonNull @NotBlank String id) {
        Optional.ofNullable(data().getCustomers().get(id))
    }

    @Override
    void deleteById(@NonNull @NotBlank String id) {
        XThreads.executeSynchronized(new Runnable() { // <2>
            @Override
            void run() {
                data().getCustomers().remove(id)
                store(data().getCustomers()) // <3>
            }
        })
    }

    private void store(Object instance) {
        storageManager.store(instance)
    }

    private Data data() {
        (Data) storageManager.root()
    }
}
//end::clazz[]
