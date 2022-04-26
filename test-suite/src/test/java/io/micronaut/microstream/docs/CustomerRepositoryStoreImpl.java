package io.micronaut.microstream.docs;

import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.microstream.annotation.Store;
import jakarta.inject.Singleton;
import one.microstream.storage.embedded.types.EmbeddedStorageManager;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Requires(property = "customer.repository", value = "store")
//tag::clazz[]
@Singleton
public class CustomerRepositoryStoreImpl implements CustomerRepository {

    private final EmbeddedStorageManager embeddedStorageManager;

    public CustomerRepositoryStoreImpl(EmbeddedStorageManager embeddedStorageManager) { // <1>
        this.embeddedStorageManager = embeddedStorageManager;
    }

    @Override
    @NonNull
    public Customer save(@NonNull @NotNull @Valid CustomerSave customerSave) {
        return addCustomer(data().getCustomers(), customerSave);
    }

    @Override
    public void update(@NonNull @NotBlank String id,
                       @NonNull @NotNull @Valid CustomerSave customerSave) {
        updateCustomer(id, customerSave);
    }

    @Override
    @NonNull
    public Optional<Customer> findById(@NonNull @NotBlank String id) {
        return Optional.ofNullable(data().getCustomers().get(id));
    }

    @Override
    public void deleteById(@NonNull @NotBlank String id) {
        removeCustomer(data().getCustomers(), id);
    }

    @Store(result = true) // <2>
    @Nullable
    protected Customer updateCustomer(@NonNull String id,
                                      @NonNull CustomerSave customerSave) {
        Customer c = data().getCustomers().get(id);
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

    @NonNull
    private Data data() {
        return (Data) embeddedStorageManager.root();
    }
}
//end::clazz[]
