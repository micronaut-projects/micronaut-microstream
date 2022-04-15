package io.micronaut.microstream.docs

import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Status
import io.micronaut.http.annotation.Delete
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.uri.UriBuilder
import java.util.Optional
import java.util.UUID
import javax.validation.Valid

@Controller("/customer")
internal class CustomerController(private val repository: CustomerRepository) {
    @Post
    fun save(@Body customerSave: @Valid CustomerSave): HttpResponse<*> {
        val customer = Customer(
            UUID.randomUUID().toString(),
            customerSave.firstName,
            customerSave.lastName
        )
        repository.save(customer)
        return HttpResponse.created<Any>(UriBuilder.of("/customer").path(customer.id).build())
    }

    @Get("/{id}")
    fun show(@PathVariable id: String): Optional<Customer> {
        return repository.findById(id)
    }

    @Delete("/{id}")
    @Status(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable id: String) {
        repository.deleteById(id)
    }
}
