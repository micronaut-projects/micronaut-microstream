package io.micronaut.microstream.docs

import io.micronaut.context.annotation.Requires
import io.micronaut.core.annotation.NonNull
import io.micronaut.core.annotation.Nullable
import io.micronaut.microstream.annotation.Store
import jakarta.inject.Singleton
import one.microstream.storage.embedded.types.EmbeddedStorageManager

import javax.validation.Valid
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Requires(property = "customer.repository", value = "store")
//tag::clazz[]
@Singleton
class CustomerRepositoryStoreImpl implements CustomerRepository {

    private final EmbeddedStorageManager embeddedStorageManager

    CustomerRepositoryStoreImpl(EmbeddedStorageManager embeddedStorageManager) { // <1>
        this.embeddedStorageManager = embeddedStorageManager
    }

    @Override
    @NonNull
    Customer save(@NonNull @NotNull @Valid CustomerSave customerSave) {
        return addCustomer(data().getCustomers(), customerSave)
    }

    @Override
    void update(@NonNull @NotNull @Valid Customer customer) {
        updateCustomer(customer)
    }

    @Override
    @NonNull
    Optional<Customer> findById(@NonNull @NotBlank String id) {
        Optional.ofNullable(data().getCustomers().get(id))
    }

    @Override
    void deleteById(@NonNull @NotBlank String id) {
        removeCustomer(data().getCustomers(), id)
    }

    @Store(result = true) // <2>
    @Nullable
    protected Customer updateCustomer(@NonNull Customer customer) {
        Customer c = data().getCustomers().get(customer.getId())
        if (c != null) {
            c.setFirstName(customer.getFirstName())
            c.setLastName(customer.getLastName())
            return c
        }
        return null
    }

    @Store(parameters = "customers") // <3>
    protected Customer addCustomer(@NonNull Map<String, Customer> customers,
                                   @NonNull CustomerSave customerSave) {
        Customer customer = new Customer(UUID.randomUUID().toString(),
            customerSave.getFirstName(),
            customerSave.getLastName())
        customers.put(customer.getId(), customer)
        customer
    }

    @Store(parameters = "customers") // <3>
    protected void removeCustomer(@NonNull Map<String, Customer> customers,
                                  @NonNull String id) {
        customers.remove(id)
    }

    @NonNull
    private Data data() {
        (Data) embeddedStorageManager.root()
    }
}
//end::clazz[]
