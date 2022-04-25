package io.micronaut.microstream.docs

import javax.validation.Valid
import javax.validation.constraints.NotBlank

interface CustomerRepository {
    fun save(customer: @Valid CustomerSave): Customer
    fun update(customer: @Valid Customer)
    fun findById(id: @NotBlank String): Customer?
    fun deleteById(id: @NotBlank String)
}
