package io.micronaut.microstream.docs

import io.micronaut.core.annotation.Introspected
import jakarta.validation.constraints.NotBlank

@Introspected
data class CustomerSave(
    @field:NotBlank var firstName: String,
    var lastName: String?)
