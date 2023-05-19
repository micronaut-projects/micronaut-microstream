package io.micronaut.microstream.docs;

import io.micronaut.context.ApplicationContext;
import io.micronaut.context.env.Environment;
import io.micronaut.core.util.CollectionUtils;
import io.micronaut.core.util.StringUtils;
import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.BlockingHttpClient;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.microstream.rest.RootObject;
import io.micronaut.runtime.server.EmbeddedServer;
import org.junit.jupiter.api.AfterEach;
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

class PostgresCustomerControllerTest {

    @ParameterizedTest
    @ValueSource(strings = {
        "store",
        "embedded-storage-manager",
        "store-with-name",
        "root-eager",
        "store-root-eager",
        "store-annotation"
    })
    void verifyCrudWithMicroStream(String customerRepositoryImplementation) throws Exception {
        // Given
        Map<String, Object> properties = Map.of(
            "customer.repository", customerRepositoryImplementation,
            "datasources.main.db-type", "postgresql",
            "micronaut.metrics.enabled", StringUtils.FALSE,
            "microstream.postgres.storage.main.table-name", "microstream",
            "microstream.postgres.storage.main.root-class", "io.micronaut.microstream.docs.Data",
            "microstream.rest.enabled", "true"
        );
        EmbeddedServer embeddedServer = ApplicationContext.run(EmbeddedServer.class, properties);
        HttpClient httpClient = embeddedServer.getApplicationContext()
            .createBean(HttpClient.class, embeddedServer.getURL());
        BlockingHttpClient client = httpClient.toBlocking();

        // And
        String sergioFirstName = "Sergio";
        String sergioLastName = "del Amo";
        String timFirstName = "Tim";

        // When we create Sergio and Tim
        String sergioLocation = create(client, sergioFirstName);
        String timLocation = create(client, timFirstName);

        // When we retrieve Sergio
        HttpRequest<?> showRequest = HttpRequest.GET(sergioLocation);
        HttpResponse<Customer> showResponse = client.exchange(showRequest, Customer.class);

        // Then
        assertEquals(HttpStatus.OK, showResponse.status());
        Customer customer = showResponse.body();
        assertNotNull(customer);
        assertEquals(sergioFirstName, customer.getFirstName());
        assertNull(customer.getLastName());

        // Check the rest-api endpoint is working
        RootObject root = client.retrieve("/microstream/root", RootObject.class);
        RootObject namedRoot = client.retrieve("/microstream/main/root", RootObject.class);
        assertEquals(root.getName(), namedRoot.getName());
        assertEquals(root.getObjectId(), namedRoot.getObjectId());

        client.retrieve("/microstream/object/" + root.getObjectId() + "?valueLength=10000&variableLength=0&references=true");
        client.retrieve("/microstream/main/object/" + root.getObjectId() + "?valueLength=10000&variableLength=0&references=true");

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

        // When
        HttpResponse<?> patchResponse = secondClient.exchange(HttpRequest.PATCH(sergioLocation, new CustomerSave(customer.getFirstName(), sergioLastName)));

        // Then
        assertEquals(HttpStatus.OK, patchResponse.status());
        assertNotNull(patchResponse.getHeaders().get(HttpHeaders.LOCATION));
        assertEquals(sergioLocation, patchResponse.getHeaders().get(HttpHeaders.LOCATION));

        // When we restart the server
        httpClient.close();
        embeddedServer.close();
        embeddedServer = ApplicationContext.run(EmbeddedServer.class, properties);
        httpClient = embeddedServer.getApplicationContext().createBean(HttpClient.class, embeddedServer.getURL());
        BlockingHttpClient thirdClient = httpClient.toBlocking();

        // And we re-retrieve Sergio
        showResponse = thirdClient.exchange(HttpRequest.GET(sergioLocation), Customer.class);

        // Then he still exists and his last name is updated
        assertEquals(HttpStatus.OK, showResponse.status());
        customer = showResponse.body();
        assertNotNull(customer);
        assertEquals(sergioFirstName, customer.getFirstName());
        assertEquals(sergioLastName, customer.getLastName());

        // When we delete Sergio
        delete(thirdClient, sergioLocation);

        // When we restart the server
        thirdClient.close();
        embeddedServer.close();
        embeddedServer = ApplicationContext.run(EmbeddedServer.class, properties);
        httpClient = embeddedServer.getApplicationContext().createBean(HttpClient.class, embeddedServer.getURL());
        BlockingHttpClient fourthClient = httpClient.toBlocking();

        // Then Sergio remains gone
        Executable e = () -> fourthClient.exchange(HttpRequest.GET(sergioLocation), Customer.class);
        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, e);
        assertEquals(HttpStatus.NOT_FOUND, thrown.getStatus());

        // But when we get Tim
        showResponse = fourthClient.exchange(HttpRequest.GET(timLocation), Customer.class);

        // Then he still exists
        assertEquals(HttpStatus.OK, showResponse.status());
        customer = showResponse.body();
        assertNotNull(customer);
        assertEquals(timFirstName, customer.getFirstName());
        assertNull(customer.getLastName());

        delete(fourthClient, timLocation);

        fourthClient.close();
        embeddedServer.close();
    }

    private static String create(BlockingHttpClient client, String firstName) {
        HttpRequest<?> request = HttpRequest.POST("/customer", Collections.singletonMap("firstName", firstName));
        HttpResponse<?> response = client.exchange(request);
        assertEquals(HttpStatus.CREATED, response.status());
        String location = response.getHeaders().get(HttpHeaders.LOCATION);
        assertNotNull(location);
        return location;
    }

    private static void delete(BlockingHttpClient client, String location) {
        HttpResponse<Customer> deleteResponse = client.exchange(HttpRequest.DELETE(location), Customer.class);
        assertEquals(HttpStatus.NO_CONTENT, deleteResponse.status());
        Executable e = () -> client.exchange(HttpRequest.GET(location), Customer.class);
        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, e);
        assertEquals(HttpStatus.NOT_FOUND, thrown.getStatus());
    }
}
