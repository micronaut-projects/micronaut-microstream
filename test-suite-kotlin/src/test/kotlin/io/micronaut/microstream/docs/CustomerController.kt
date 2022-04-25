package io.micronaut.microstream.docs

import io.micronaut.core.annotation.NonNull
import io.micronaut.http.HttpHeaders
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.MutableHttpResponse
import io.micronaut.http.annotation.*
import io.micronaut.http.uri.UriBuilder
import javax.validation.Valid
import javax.validation.constraints.NotNull

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
    fun update(@Body customer: @Valid Customer): MutableHttpResponse<*>? {
        repository.update(customer)
        return HttpResponse.ok<Any>()
            .header(HttpHeaders.LOCATION,
                UriBuilder.of("/customer").path(customer.id).build().toString())
    }

    @Delete("/{id}")
    @Status(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable id: String) {
        repository.deleteById(id)
    }
}
