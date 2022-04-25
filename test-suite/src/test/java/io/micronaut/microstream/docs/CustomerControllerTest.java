package io.micronaut.microstream.docs;

import io.micronaut.context.ApplicationContext;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.util.CollectionUtils;
import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.BlockingHttpClient;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.runtime.server.EmbeddedServer;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CustomerControllerTest {

    @ParameterizedTest
    //@ValueSource(strings = {"embedded-storage-manager", "store"})
    @ValueSource(strings = {"store"})
    void verifyCrudWithMicrostream(String customerRepositoryImplementation) throws Exception {
        // Given
        String storageDirectory = "build/microstream-" + UUID.randomUUID();
        Map<String, Object> properties = CollectionUtils.mapOf(
            "microstream.storage.main.storage-directory", storageDirectory,
            "customer.repository",
            customerRepositoryImplementation,
            "microstream.storage.main.root-class",
            "io.micronaut.microstream.docs.Data");
        EmbeddedServer embeddedServer = ApplicationContext.run(EmbeddedServer.class, properties);
        HttpClient httpClient = embeddedServer.getApplicationContext()
            .createBean(HttpClient.class, embeddedServer.getURL());
        BlockingHttpClient client = httpClient.toBlocking();

        // And
        String sergioFirstName = "Sergio";
        String timFirstName = "Tim";

        // When we create Sergio
        HttpRequest<?> request = HttpRequest.POST("/customer", Collections.singletonMap("firstName", sergioFirstName));
        HttpResponse<?> response = client.exchange(request);

        // Then
        assertEquals(HttpStatus.CREATED, response.status());
        String sergioLocation = response.getHeaders().get(HttpHeaders.LOCATION);
        assertNotNull(sergioLocation);

        // When we create Tim
        request = HttpRequest.POST("/customer", Collections.singletonMap("firstName", timFirstName));
        response = client.exchange(request);
        assertEquals(HttpStatus.CREATED, response.status());

        // Then
        String timLocation = response.getHeaders().get(HttpHeaders.LOCATION);
        assertNotNull(timLocation);

        // When we retrieve Sergio
        HttpRequest<?> showRequest = HttpRequest.GET(sergioLocation);
        HttpResponse<Customer> showResponse = client.exchange(showRequest, Customer.class);

        // Then
        assertEquals(HttpStatus.OK, showResponse.status());
        Customer customer = showResponse.body();
        assertNotNull(customer);
        assertEquals(sergioFirstName, customer.getFirstName());
        assertNull(customer.getLastName());

        // When we restart the server
        httpClient.close();
        embeddedServer.close();
        embeddedServer = ApplicationContext.run(EmbeddedServer.class, properties);
        httpClient = embeddedServer.getApplicationContext().createBean(HttpClient.class, embeddedServer.getURL());
        BlockingHttpClient secondClient = httpClient.toBlocking();

        // And we re-retrieve Sergio
        showResponse = secondClient.exchange(HttpRequest.GET(sergioLocation), Customer.class);

        // Then he still exists
        assertEquals(HttpStatus.OK, showResponse.status());
        customer = showResponse.body();
        assertNotNull(customer);
        assertEquals(sergioFirstName, customer.getFirstName());
        assertNull(customer.getLastName());

        // When we delete Sergio
        HttpResponse<Customer> deleteResponse = secondClient.exchange(HttpRequest.DELETE(sergioLocation), Customer.class);

        // Then
        assertEquals(HttpStatus.NO_CONTENT, deleteResponse.status());

        // And Sergio is gone
        Executable e = () -> secondClient.exchange(HttpRequest.GET(sergioLocation), Customer.class);
        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, e);
        assertEquals(HttpStatus.NOT_FOUND, thrown.getStatus());

        // When we restart the server
        secondClient.close();
        embeddedServer.close();
        embeddedServer = ApplicationContext.run(EmbeddedServer.class, properties);
        httpClient = embeddedServer.getApplicationContext().createBean(HttpClient.class, embeddedServer.getURL());
        BlockingHttpClient thirdClient = httpClient.toBlocking();

        // Then Sergio remains gone
        e = () -> thirdClient.exchange(HttpRequest.GET(sergioLocation), Customer.class);
        thrown = assertThrows(HttpClientResponseException.class, e);
        assertEquals(HttpStatus.NOT_FOUND, thrown.getStatus());

        // But when we get Tim
        showResponse = thirdClient.exchange(HttpRequest.GET(timLocation), Customer.class);

        // Then he still exists
        assertEquals(HttpStatus.OK, showResponse.status());
        customer = showResponse.body();
        assertNotNull(customer);
        assertEquals(timFirstName, customer.getFirstName());
        assertNull(customer.getLastName());

        thirdClient.close();
        embeddedServer.close();
    }
}
