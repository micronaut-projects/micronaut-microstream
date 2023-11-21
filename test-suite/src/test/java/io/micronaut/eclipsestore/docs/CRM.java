package io.micronaut.eclipsestore.docs;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public class CRM {
    private Customers customers = new Customers();

    public Customers getCustomers() {
        return customers;
    }
}
