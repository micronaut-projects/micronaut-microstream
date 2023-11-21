package io.micronaut.eclipsestore.docs

import io.micronaut.core.annotation.Introspected
import io.micronaut.core.annotation.NonNull
import io.micronaut.core.annotation.Nullable
import jakarta.validation.constraints.NotBlank

@Introspected
class CustomerSave {

    @NonNull
    @NotBlank
    String firstName

    @Nullable
    String lastName
}



