package io.micronaut.microstream.docs;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

@Introspected // <1>
public class Data {
    private Map<String, Customer> customers = new HashMap<>();

    @NonNull
    public Map<String, Customer> getCustomers() {
        return this.customers;
    }
}
