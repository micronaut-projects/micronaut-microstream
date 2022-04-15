package io.micronaut.microstream.docs

import io.micronaut.core.annotation.NonNull
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Delete
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.Post
import io.micronaut.http.annotation.Status
import io.micronaut.http.uri.UriBuilder

import javax.validation.Valid
import javax.validation.constraints.NotNull

@Controller("/customer")
class CustomerController {

    private final CustomerRepository repository

    CustomerController(CustomerRepository repository) {
        this.repository = repository
    }

    @Post
    HttpResponse<?> save(@NonNull @NotNull @Valid @Body CustomerSave customerSave) {
        Customer customer = new Customer(UUID.randomUUID().toString(), customerSave.firstName, customerSave.lastName)
        repository.save(customer)
        HttpResponse.created(UriBuilder.of("/customer").path(customer.id).build())
    }

    @Get("/{id}")
    Optional<Customer> show(@PathVariable @NonNull String id) {
        repository.findById(id)
    }

    @Delete("/{id}")
    @Status(HttpStatus.NO_CONTENT)
    void delete(@PathVariable @NonNull String id) {
        repository.deleteById(id)
    }
}
