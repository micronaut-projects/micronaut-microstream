package io.micronaut.microstream.docs;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.NonNull;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Introspected
public class Customers {
    @NonNull
    private Map<String, Customer> customersById = new ConcurrentHashMap<>();

    @NonNull
    public Map<String, Customer> getCustomersById() {
        return customersById;
    }
}
