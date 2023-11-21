package io.micronaut.eclipsestore.docs

import io.micronaut.context.annotation.Requires
import io.micronaut.core.annotation.NonNull
import io.micronaut.core.annotation.Nullable
import io.micronaut.eclipsestore.RootProvider
import io.micronaut.eclipsestore.annotations.StoreParams
import io.micronaut.eclipsestore.annotations.StoreReturn
import jakarta.inject.Singleton
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

@Requires(property = "customer.repository", value = "store")
//tag::clazz[]
@Singleton
class CustomerRepositoryStoreImpl implements CustomerRepository {

    private final RootProvider<Data> rootProvider

    CustomerRepositoryStoreImpl(RootProvider<Data> rootProvider) { // <1>
        this.rootProvider = rootProvider
    }

    @Override
    @NonNull
    Customer save(@NonNull @NotNull @Valid CustomerSave customerSave) {
        return addCustomer(rootProvider.root().customers, customerSave)
    }

    @Override
    void update(@NonNull @NotBlank String id,
                @NonNull @NotNull @Valid CustomerSave customerSave) {
        updateCustomer(id, customerSave)
    }

    @Override
    @NonNull
    Optional<Customer> findById(@NonNull @NotBlank String id) {
        Optional.ofNullable(rootProvider.root().customers[id])
    }

    @Override
    void deleteById(@NonNull @NotBlank String id) {
        removeCustomer(rootProvider.root().customers, id)
    }

    @StoreReturn // <2>
    @Nullable
    protected Customer updateCustomer(@NonNull String id,
                                      @NonNull CustomerSave customerSave) {
        Customer c = rootProvider.root().customers[id]
        if (c != null) {
            c.with {
                firstName = customerSave.firstName
                lastName = customerSave.lastName
            }
            return c
        }
        null
    }

    @StoreParams("customers") // <3>
    protected Customer addCustomer(@NonNull Map<String, Customer> customers,
                                   @NonNull CustomerSave customerSave) {
        Customer customer = new Customer(UUID.randomUUID().toString(),
            customerSave.firstName,
            customerSave.lastName)
        customers[customer.id] = customer
        customer
    }

    @StoreParams("customers") // <3>
    protected void removeCustomer(@NonNull Map<String, Customer> customers,
                                  @NonNull String id) {
        customers.remove(id)
    }
}
//end::clazz[]
