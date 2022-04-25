package io.micronaut.microstream.docs

import groovy.transform.Canonical
import io.micronaut.context.ApplicationContext
import io.micronaut.http.HttpHeaders
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.BlockingHttpClient
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.runtime.server.EmbeddedServer
import spock.lang.Specification
import spock.lang.Unroll

class CustomerControllerSpec extends Specification {

    @Unroll
    void "verify CRUD with Microstream"(String customerRepositoryImplementation) {
        given:
        String storageDirectory = "build/microstream-" + UUID.randomUUID();
        ServerAndClient server = startServer(serverProperties(storageDirectory, customerRepositoryImplementation))

        and:
        String sergioName = "Sergio"
        String timName = "Tim"

        when:
        String sergioLocation = createCustomer(server.client, sergioName)

        then:
        sergioLocation

        when:
        String timLocation = createCustomer(server.client, timName)

        then:
        timLocation
        timLocation != sergioLocation

        when:
        Customer customer = getCustomer(server.client, sergioLocation)

        then:
        with(customer) {
            firstName == sergioName
            !lastName
        }

        when:
        customer = getCustomer(server.client, timLocation)

        then:
        with(customer) {
            firstName == timName
            !lastName
        }

        when: "we restart the server"
        server = startServer(serverProperties(storageDirectory, customerRepositoryImplementation), server)
        customer = getCustomer(server.client, sergioLocation)

        then: "fetch Sergio, he still exists"
        with(customer) {
            firstName == sergioName
            !lastName
        }

        when:
        deleteCustomer(server.client, sergioLocation)

        and:
        server.client.exchange(HttpRequest.GET(sergioLocation))

        then:
        HttpClientResponseException e = thrown()
        HttpStatus.NOT_FOUND == e.status

        when: "we restart the server again"
        server = startServer(serverProperties(storageDirectory, customerRepositoryImplementation), server)

        and: "try to fetch Sergio"
        server.client.exchange(HttpRequest.GET(sergioLocation))

        then: "he is still gone"
        e = thrown(HttpClientResponseException)
        HttpStatus.NOT_FOUND == e.status

        when: "we try to get Tim"
        customer = getCustomer(server.client, timLocation)

        then: "he is still there"
        with(customer) {
            firstName == timName
            !lastName
        }

        cleanup:
        server.close()

        where:
        customerRepositoryImplementation << ["embedded-storage-manager"]
    }

    private static Map<String, String> serverProperties(String storageDirectory, String customerRepositoryImplementation) {
        ["microstream.storage.one-microstream-instance.storage-directory": storageDirectory, "customer.repository": customerRepositoryImplementation]
    }

    @Canonical
    class ServerAndClient implements AutoCloseable {
        EmbeddedServer server
        BlockingHttpClient client

        @Override
        void close() throws Exception {
            client?.close()
            server?.close()
        }
    }

    ServerAndClient startServer(Map<String, Object> properties, ServerAndClient serverAndClient = null) {
        serverAndClient?.close()
        EmbeddedServer embeddedServer = ApplicationContext.run(EmbeddedServer.class, properties)
        HttpClient httpClient = embeddedServer.applicationContext.createBean(HttpClient.class, embeddedServer.URL)
        new ServerAndClient(embeddedServer, httpClient.toBlocking())
    }

    String createCustomer(BlockingHttpClient client, String firstName) {
        HttpResponse<?> response = client.exchange(HttpRequest.POST("/customer", [firstName: firstName]))
        assert HttpStatus.CREATED == response.status()
        response.getHeaders().get(HttpHeaders.LOCATION)
    }

    void deleteCustomer(BlockingHttpClient client, String location) {
        HttpResponse<Customer> deleteResponse = client.exchange(HttpRequest.DELETE(location), Customer)
        assert HttpStatus.NO_CONTENT == deleteResponse.status()
    }

    Customer getCustomer(BlockingHttpClient client, String location) {
        HttpResponse<Customer> showResponse = client.exchange(HttpRequest.GET(location), Customer)
        assert HttpStatus.OK == showResponse.status()
        showResponse.body()
    }
}
