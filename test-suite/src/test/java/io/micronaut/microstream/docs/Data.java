package io.micronaut.microstream.docs;

import io.micronaut.core.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

public class Data {
    private Map<String, Customer> customers = new HashMap<>();

    @NonNull
    public Map<String, Customer> getCustomers() {
        return this.customers;
    }
}
