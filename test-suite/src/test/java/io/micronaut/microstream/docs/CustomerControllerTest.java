package io.micronaut.microstream.docs;

import io.micronaut.context.BeanContext;
import io.micronaut.context.annotation.Property;
import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.BlockingHttpClient;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.Collections;
import static org.junit.jupiter.api.Assertions.*;

@Property(name = "microstream.storage.main.root-class", value = "io.micronaut.microstream.docs.Data")
@Property(name = "microstream.storage.main.storage-directory", value = "build/microstream")
@MicronautTest
class CustomerControllerTest {
    @Inject
    @Client("/")
    HttpClient httpClient;

    @Inject
    BeanContext beanContext;

    @Test
    void verifyCrudWithMicrostream() {
        String firstName = "Sergio";
        BlockingHttpClient client = httpClient.toBlocking();
        HttpRequest<?> request = HttpRequest.POST("/customer", Collections.singletonMap("firstName", firstName));
        HttpResponse<?> response = client.exchange(request);
        assertEquals(HttpStatus.CREATED, response.status());
        String location = response.getHeaders().get(HttpHeaders.LOCATION);
        assertNotNull(location);
        HttpRequest<?> showRequest = HttpRequest.GET(location);
        HttpResponse<Customer> showResponse = client.exchange(showRequest, Customer.class);
        assertEquals(HttpStatus.OK, showResponse.status());
        Customer customer = showResponse.body();
        assertNotNull(customer);
        assertEquals(firstName, customer.getFirstName());
        assertNull(customer.getLastName());
        HttpResponse<Customer> deleteResponse = client.exchange(HttpRequest.DELETE(location), Customer.class);
        assertEquals(HttpStatus.NO_CONTENT, deleteResponse.status());
        Executable e = () -> client.exchange(showRequest);
        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, e);
        assertEquals(HttpStatus.NOT_FOUND, thrown.getStatus());
    }
}
