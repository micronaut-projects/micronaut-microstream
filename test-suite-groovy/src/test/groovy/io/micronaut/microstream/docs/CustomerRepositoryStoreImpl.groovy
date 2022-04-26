package io.micronaut.microstream.docs

import io.micronaut.context.annotation.Requires
import io.micronaut.core.annotation.NonNull
import io.micronaut.core.annotation.Nullable
import io.micronaut.microstream.annotation.Store
import jakarta.inject.Singleton
import one.microstream.storage.types.StorageManager

import javax.validation.Valid
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Requires(property = "customer.repository", value = "store")
//tag::clazz[]
@Singleton
class CustomerRepositoryStoreImpl implements CustomerRepository {

    private final StorageManager storageManager

    CustomerRepositoryStoreImpl(StorageManager storageManager) { // <1>
        this.storageManager = storageManager
    }

    @Override
    @NonNull
    Customer save(@NonNull @NotNull @Valid CustomerSave customerSave) {
        return addCustomer(data().getCustomers(), customerSave)
    }

    @Override
    void update(@NonNull @NotBlank String id,
                @NonNull @NotNull @Valid CustomerSave customerSave) {
        updateCustomer(id, customerSave)
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
    protected Customer updateCustomer(@NonNull String id,
                                      @NonNull CustomerSave customerSave) {
        Customer c = data().getCustomers().get(id)
        if (c != null) {
            c.with {
                firstName = customerSave.firstName
                lastName = customerSave.lastName
            }
            return c
        }
        null
    }

    @Store(parameters = "customers") // <3>
    protected Customer addCustomer(@NonNull Map<String, Customer> customers,
                                   @NonNull CustomerSave customerSave) {
        Customer customer = new Customer(UUID.randomUUID().toString(),
            customerSave.firstName,
            customerSave.lastName)
        customers[customer.id] = customer
        customer
    }

    @Store(parameters = "customers") // <3>
    protected void removeCustomer(@NonNull Map<String, Customer> customers,
                                  @NonNull String id) {
        customers.remove(id)
    }

    @NonNull
    private Data data() {
        (Data) storageManager.root()
    }
}
//end::clazz[]
