package io.micronaut.microstream.docs;

import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.microstream.RootProvider;
import io.micronaut.microstream.annotations.Store;
import jakarta.inject.Singleton;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Requires(property = "customer.repository", value = "store-annotation")
//tag::clazz[]
@Singleton
public class CustomerRepositoryStoreAnnotationImpl implements CustomerRepository {

    private final RootProvider<Data> rootProvider;

    public CustomerRepositoryStoreAnnotationImpl(RootProvider<Data> rootProvider) { // <1>
        this.rootProvider = rootProvider;
    }

    @Override
    @NonNull
    public Customer save(@NonNull @NotNull @Valid CustomerSave customerSave) {
        return addCustomer(rootProvider.root().getCustomers(), customerSave);
    }

    @Override
    public void update(@NonNull @NotBlank String id,
                       @NonNull @NotNull @Valid CustomerSave customerSave) {
        updateCustomer(id, customerSave);
    }

    @Override
    @NonNull
    public Optional<Customer> findById(@NonNull @NotBlank String id) {
        return Optional.ofNullable(rootProvider.root().getCustomers().get(id));
    }

    @Override
    public void deleteById(@NonNull @NotBlank String id) {
        removeCustomer(rootProvider.root().getCustomers(), id);
    }

    @Store(result = true) // <2>
    @Nullable
    protected Customer updateCustomer(@NonNull String id,
                                      @NonNull CustomerSave customerSave) {
        Customer c = rootProvider.root().getCustomers().get(id);
        if (c != null) {
            c.setFirstName(customerSave.getFirstName());
            c.setLastName(customerSave.getLastName());
            return c;
        }
        return null;
    }

    @Store(parameters = "customers") // <3>
    protected Customer addCustomer(@NonNull Map<String, Customer> customers,
                                   @NonNull CustomerSave customerSave) {
        Customer customer = new Customer(UUID.randomUUID().toString(),
            customerSave.getFirstName(),
            customerSave.getLastName());
        customers.put(customer.getId(), customer);
        return customer;
    }

    @Store(parameters = "customers") // <3>
    protected void removeCustomer(@NonNull Map<String, Customer> customers,
                                  @NonNull String id) {
        customers.remove(id);
    }
}
//end::clazz[]
