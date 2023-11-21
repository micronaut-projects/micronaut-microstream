package io.micronaut.eclipsestore.docs;

import io.micronaut.core.annotation.NonNull;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public interface CrmCustomerService {
    @NonNull
    Customers save(@NonNull @NotNull @Valid Customer customer);
}
