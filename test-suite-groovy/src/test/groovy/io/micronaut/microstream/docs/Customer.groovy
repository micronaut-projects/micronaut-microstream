package io.micronaut.microstream.docs

import io.micronaut.serde.annotation.Serdeable
import io.micronaut.core.annotation.NonNull
import io.micronaut.core.annotation.Nullable

import jakarta.validation.constraints.NotBlank

@Serdeable // <1>
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
