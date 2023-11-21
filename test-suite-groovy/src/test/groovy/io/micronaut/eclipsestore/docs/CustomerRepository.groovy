package io.micronaut.eclipsestore.docs

import io.micronaut.core.annotation.NonNull

import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

interface CustomerRepository {

    @NonNull
    Customer save(@NonNull @NotNull @Valid CustomerSave customer)

    void update(@NonNull @NotBlank String id,
                @NonNull @NotNull @Valid CustomerSave customer)

    @NonNull
    Optional<Customer> findById(@NonNull @NotBlank String id)

    void deleteById(@NonNull @NotBlank String id)
}
