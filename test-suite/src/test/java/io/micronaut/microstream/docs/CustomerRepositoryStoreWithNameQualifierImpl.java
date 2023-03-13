package io.micronaut.microstream.docs;

import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.microstream.annotations.Store;
import jakarta.inject.Singleton;
import one.microstream.storage.types.StorageManager;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Requires(property = "customer.repository", value = "store-with-name")
//tag::clazz[]
@Singleton
public class CustomerRepositoryStoreWithNameQualifierImpl implements CustomerRepository {

    private final StorageManager storageManager;

    public CustomerRepositoryStoreWithNameQualifierImpl(StorageManager storageManager) { // <1>
        this.storageManager = storageManager;
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

    @Store(result = true, name = "main") // <2>
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

    @Store(parameters = "customers", name = "main") // <3>
    protected Customer addCustomer(@NonNull Map<String, Customer> customers,
                                   @NonNull CustomerSave customerSave) {
        Customer customer = new Customer(UUID.randomUUID().toString(),
            customerSave.getFirstName(),
            customerSave.getLastName());
        customers.put(customer.getId(), customer);
        return customer;
    }

    @Store(parameters = "customers", name = "main") // <3>
    protected void removeCustomer(@NonNull Map<String, Customer> customers,
                                  @NonNull String id) {
        customers.remove(id);
    }

    @NonNull
    private Data data() {
        return (Data) storageManager.root();
    }
}
//end::clazz[]
