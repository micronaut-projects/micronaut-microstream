package io.micronaut.microstream.docs

import javax.validation.Valid
import javax.validation.constraints.NotBlank

interface CustomerRepository {
    fun save(customer: @Valid Customer)
    fun findByFirstName(firstName: @NotBlank String): Collection<Customer>
    fun findById(id: @NotBlank String): Customer?
    fun deleteById(id: @NotBlank String)
}
