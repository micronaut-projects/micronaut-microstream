package io.micronaut.eclipsestore.docs;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.serde.annotation.Serdeable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Serdeable
public class Customers {
    @NonNull
    private Map<String, Customer> customersById = new ConcurrentHashMap<>();

    @NonNull
    public Map<String, Customer> getCustomersById() {
        return customersById;
    }
}
