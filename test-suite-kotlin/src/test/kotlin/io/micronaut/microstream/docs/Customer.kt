package io.micronaut.microstream.docs

import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.NotBlank

@Introspected
data class Customer(
    @field:NotBlank var id: String,
    @field:NotBlank var firstName: String,
    var lastName: String?)
