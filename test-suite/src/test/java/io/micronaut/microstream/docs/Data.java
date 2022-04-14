package io.micronaut.microstream.docs;

import io.micronaut.core.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Data {
    private Map<String, Customer> customers = new HashMap<>();

    @NonNull
    public Optional<Customer> findById(@NonNull String id) {
        return Optional.ofNullable(customers.get(id));
    }

    @NonNull
    public Collection<Customer> getCustomers() {
        return customers.values();
    }

    public void add(@NonNull Customer customer) {
        this.customers.put(customer.getId(), customer);
    }

    public void remove(@NonNull String id) {
        this.customers.remove(id);
    }
}
