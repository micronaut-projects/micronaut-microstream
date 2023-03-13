package io.micronaut.microstream.docs;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Patch;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Status;
import io.micronaut.http.uri.UriBuilder;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.Optional;
import java.util.UUID;

@Controller("/customer")
class CustomerController {

    private final CustomerRepository repository;

    CustomerController(CustomerRepository repository) {
        this.repository = repository;
    }

    @Post
    HttpResponse<?> save(@NonNull @NotNull @Valid @Body CustomerSave customerSave) {
        Customer customer = repository.save(customerSave);
        return HttpResponse.created(UriBuilder.of("/customer").path(customer.getId()).build());
    }

    @Patch("/{id}")
    MutableHttpResponse<?> update(@PathVariable @NonNull String id,
                                  @NonNull @NotNull @Valid @Body CustomerSave customer) {
        repository.update(id, customer);
        return HttpResponse.ok()
            .header(HttpHeaders.LOCATION, UriBuilder.of("/customer")
                .path(id)
                .build().toString());
    }

    @Get("/{id}")
    Optional<Customer> show(@PathVariable @NonNull String id) {
        return repository.findById(id);
    }

    @Delete("/{id}")
    @Status(HttpStatus.NO_CONTENT)
    void delete(@PathVariable @NonNull String id) {
        repository.deleteById(id);
    }
}
