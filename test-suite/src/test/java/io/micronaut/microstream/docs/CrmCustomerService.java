package io.micronaut.microstream.docs;

import io.micronaut.core.annotation.NonNull;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public interface CrmCustomerService {
    @NonNull
    Customers save(@NonNull @NotNull @Valid Customer customer);
}
