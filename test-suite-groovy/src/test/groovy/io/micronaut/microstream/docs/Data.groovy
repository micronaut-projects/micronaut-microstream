package io.micronaut.microstream.docs

import io.micronaut.core.annotation.NonNull

class Data {
    private Map<String, Customer> customers = new HashMap<>()

    @NonNull
    Optional<Customer> findById(@NonNull String id) {
        Optional.ofNullable(customers[id])
    }

    @NonNull
    Collection<Customer> getCustomers() {
        customers.values()
    }

    void add(@NonNull Customer customer) {
        this.customers[customer.id] = customer
    }

    void remove(@NonNull String id) {
        this.customers.remove(id)
    }
}
