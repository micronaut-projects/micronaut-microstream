package io.micronaut.eclipsestore.docs

import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank

interface CustomerRepository {
    fun save(customerSave: @Valid CustomerSave): Customer
    fun update(id: @NotBlank String, customerSave: @Valid CustomerSave)
    fun findById(id: @NotBlank String): Customer?
    fun deleteById(id: @NotBlank String)
}
