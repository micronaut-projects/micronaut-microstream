package io.micronaut.microstream.docs;

import io.micronaut.core.annotation.NonNull;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public interface CustomerRepository {

    @SuppressWarnings("UnusedReturnValue")
    Map<String, Customer> save(@NonNull @NotNull @Valid Customer customer);

    @NonNull
    Collection<Customer> findByFirstName(@NonNull @NotBlank String firstName);

    @NonNull
    Optional<Customer> findById(@NonNull @NotBlank String id);

    @SuppressWarnings("UnusedReturnValue")
    Map<String, Customer> deleteById(@NonNull @NotBlank String id);
}
