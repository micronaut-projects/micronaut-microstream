package io.micronaut.microstream.docs;

import io.micronaut.core.annotation.Introspected;

@Introspected
public class CRM {
    private Customers customers = new Customers();

    public Customers getCustomers() {
        return customers;
    }
}
