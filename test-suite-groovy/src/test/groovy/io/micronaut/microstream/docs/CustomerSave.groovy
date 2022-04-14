package io.micronaut.microstream.docs

import io.micronaut.core.annotation.Introspected
import io.micronaut.core.annotation.NonNull
import io.micronaut.core.annotation.Nullable
import javax.validation.constraints.NotBlank

@Introspected
class CustomerSave {

    @NonNull
    @NotBlank
    String firstName

    @Nullable
    String lastName
}



