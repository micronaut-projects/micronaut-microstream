package io.micronaut.microstream.docs

import io.micronaut.core.annotation.Introspected
import io.micronaut.core.annotation.NonNull
import io.micronaut.core.annotation.Nullable

import jakarta.validation.constraints.NotBlank

@Introspected
class Customer {
    @NonNull
    @NotBlank
    String id

    @NonNull
    @NotBlank
    String firstName

    @Nullable
    String lastName

    Customer(@NonNull String id, @NonNull String firstName, @Nullable String lastName) {
        this.id = id
        this.firstName = firstName
        this.lastName = lastName
    }
}
