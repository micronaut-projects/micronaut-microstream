package io.micronaut.microstream.docs

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

class CustomerControllerSpec extends Specification {

    void "verify CRUD with Microstream"() {
        given:
        String storageDirectory = "build/microstream-" + UUID.randomUUID();
        def (embeddedServer, client) = startServer(
                "microstream.storage.one-microstream-instance.storage-directory": storageDirectory
        )

        and:
        String sergioName = "Sergio"
        String timName = "Tim"

        when:
        String sergioLocation = createCustomer(client, sergioName)

        then:
        sergioLocation

        when:
        String timLocation = createCustomer(client, timName)

        then:
        timLocation
        timLocation != sergioLocation

        when:
        Customer customer = getCustomer(client, sergioLocation)

        then:
        with(customer) {
            firstName == sergioName
            !lastName
        }

        when:
        customer = getCustomer(client, timLocation)

        then:
        with(customer) {
            firstName == timName
            !lastName
        }

        when: "we restart the server"
        (embeddedServer, client) = startServer(embeddedServer, client,
                "microstream.storage.one-microstream-instance.storage-directory": storageDirectory,
        )

        and: "fetch Sergio, he still exists"
        customer = getCustomer(client, sergioLocation)

        then:
        with(customer) {
            firstName == sergioName
            !lastName
        }

        when:
        deleteCustomer(client, sergioLocation)

        and:
        client.exchange(HttpRequest.GET(sergioLocation))

        then:
        HttpClientResponseException e = thrown()
        HttpStatus.NOT_FOUND == e.status

        when: "we restart the server again"
        (embeddedServer, client) = startServer(embeddedServer, client,
                "microstream.storage.one-microstream-instance.storage-directory": storageDirectory,
        )

        and: "try to fetch Sergio"
        client.exchange(HttpRequest.GET(sergioLocation))

        then: "he is still gone"
        e = thrown(HttpClientResponseException)
        HttpStatus.NOT_FOUND == e.status

        when: "we try to get Tim"
        customer = getCustomer(client, timLocation)

        then: "he is still there"
        with(customer) {
            firstName == timName
            !lastName
        }

        cleanup:
        client.close()
        embeddedServer.close()
    }

    def startServer(Map<String, Object> properties, EmbeddedServer existingServer = null, BlockingHttpClient existingClient = null) {
        existingClient?.close()
        existingServer?.close()
        EmbeddedServer embeddedServer = ApplicationContext.run(EmbeddedServer.class, properties)
        HttpClient httpClient = embeddedServer.applicationContext.createBean(HttpClient.class, embeddedServer.URL)
        [embeddedServer, httpClient.toBlocking()]
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
