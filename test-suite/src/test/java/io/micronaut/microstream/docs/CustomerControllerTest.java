package io.micronaut.microstream.docs;

import io.micronaut.context.ApplicationContext;
import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.BlockingHttpClient;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.runtime.server.EmbeddedServer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CustomerControllerTest {

    @Test
    void verifyCrudWithMicrostream() {
        String firstName = "Sergio";
        String storageDirectory = "build/microstream-" + UUID.randomUUID();
        Map<String, Object> properties = Collections.singletonMap(
            "microstream.storage.one-microstream-instance.storage-directory", storageDirectory);

        EmbeddedServer embeddedServer = ApplicationContext.run(EmbeddedServer.class, properties);
        HttpClient httpClient = embeddedServer.getApplicationContext()
            .createBean(HttpClient.class, embeddedServer.getURL());
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

        httpClient.close();
        embeddedServer.close();

        EmbeddedServer secondServer = ApplicationContext.run(EmbeddedServer.class, properties);
        HttpClient secondHttpClient = secondServer.getApplicationContext().createBean(HttpClient.class, secondServer.getURL());

        BlockingHttpClient secondClient = secondHttpClient.toBlocking();


        showResponse = secondClient.exchange(HttpRequest.GET(location), Customer.class);
        assertEquals(HttpStatus.OK, showResponse.status());

        customer = showResponse.body();
        assertNotNull(customer);
        assertEquals(firstName, customer.getFirstName());
        assertNull(customer.getLastName());

        HttpResponse<Customer> deleteResponse = secondClient.exchange(HttpRequest.DELETE(location), Customer.class);
        assertEquals(HttpStatus.NO_CONTENT, deleteResponse.status());

        Executable e = () -> secondClient.exchange(HttpRequest.GET(location), Customer.class);
        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, e);
        assertEquals(HttpStatus.NOT_FOUND, thrown.getStatus());

        secondHttpClient.close();
        secondServer.close();
    }
}
