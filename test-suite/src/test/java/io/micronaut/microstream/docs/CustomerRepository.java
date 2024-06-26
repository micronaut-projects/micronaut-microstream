package io.micronaut.microstream.docs;

import io.micronaut.core.annotation.NonNull;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository {

    @NonNull
    Customer save(@NonNull @NotNull @Valid CustomerSave customerSave);

    void update(@NonNull @NotBlank String id,
                @NonNull @NotNull @Valid CustomerSave customerSave);

    @NonNull
    Optional<Customer> findById(@NonNull @NotBlank String id);

    void deleteById(@NonNull @NotBlank String id);
}
