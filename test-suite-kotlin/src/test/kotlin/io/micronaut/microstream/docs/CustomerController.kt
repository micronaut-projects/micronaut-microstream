package io.micronaut.microstream.docs

import io.micronaut.http.HttpHeaders
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.MutableHttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post
import io.micronaut.http.annotation.Patch
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Delete
import io.micronaut.http.annotation.Status
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.uri.UriBuilder
import jakarta.validation.Valid

@Controller("/customer")
internal class CustomerController(private val repository: CustomerRepository) {
    @Post
    fun save(@Body customerSave: @Valid CustomerSave): HttpResponse<*> {
        val customer = repository.save(customerSave)
        return HttpResponse.created<Any>(UriBuilder.of("/customer").path(customer.id).build())
    }

    @Get("/{id}")
    fun show(@PathVariable id: String): Customer? {
        return repository.findById(id)
    }

    @Patch("/{id}")
    fun update(@PathVariable id: String,
               @Body customer: @Valid CustomerSave): MutableHttpResponse<*>? {
        repository.update(id, customer)
        return HttpResponse.ok<Any>()
            .header(HttpHeaders.LOCATION,
                UriBuilder.of("/customer").path(id).build().toString())
    }

    @Delete("/{id}")
    @Status(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable id: String) {
        repository.deleteById(id)
    }
}
