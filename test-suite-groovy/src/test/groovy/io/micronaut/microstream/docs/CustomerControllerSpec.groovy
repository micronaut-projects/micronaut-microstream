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
        Map<String, Object> properties = Collections.singletonMap(
                "microstream.storage.one-microstream-instance.storage-directory", storageDirectory);
        EmbeddedServer embeddedServer = ApplicationContext.run(EmbeddedServer.class, properties);
        HttpClient httpClient = embeddedServer.getApplicationContext()
                .createBean(HttpClient.class, embeddedServer.getURL());
        BlockingHttpClient client = httpClient.toBlocking();

        and:
        String sergioName = "Sergio"
        String timName = "Tim"

        when:
        HttpResponse<?> response = client.exchange(HttpRequest.POST("/customer", Collections.singletonMap("firstName", sergioName)))

        then:
        HttpStatus.CREATED == response.status()

        when:
        String sergioLocation = response.getHeaders().get(HttpHeaders.LOCATION)

        then:
        sergioLocation

        when:
        response = client.exchange(HttpRequest.POST("/customer", Collections.singletonMap("firstName", timName)))

        then:
        HttpStatus.CREATED == response.status()

        when:
        String timLocation = response.getHeaders().get(HttpHeaders.LOCATION)

        then:
        timLocation
        timLocation != sergioLocation

        when:
        HttpResponse<Customer> showResponse = client.exchange(HttpRequest.GET(sergioLocation), Customer)

        then:
        HttpStatus.OK == showResponse.status()

        when:
        Customer customer = showResponse.body()

        then:
        customer
        sergioName == customer.firstName
        !customer.lastName

        when:
        showResponse = client.exchange(HttpRequest.GET(timLocation), Customer)

        then:
        HttpStatus.OK == showResponse.status()

        when:
        customer = showResponse.body()

        then:
        customer
        timName == customer.firstName
        !customer.lastName

        when: "we restart the server"
        httpClient.close();
        embeddedServer.close();
        embeddedServer = ApplicationContext.run(EmbeddedServer.class, properties);
        httpClient = embeddedServer.getApplicationContext().createBean(HttpClient.class, embeddedServer.getURL());
        client = httpClient.toBlocking();

        and: "fetch Sergio, he still exists"
        showResponse = client.exchange(HttpRequest.GET(sergioLocation), Customer)

        then:
        HttpStatus.OK == showResponse.status()

        when:
        customer = showResponse.body()

        then:
        customer
        sergioName == customer.firstName
        !customer.lastName

        when: "we delete Sergio"
        HttpResponse<Customer> deleteResponse = client.exchange(HttpRequest.DELETE(sergioLocation), Customer)

        then:
        HttpStatus.NO_CONTENT == deleteResponse.status()

        when:
        client.exchange(HttpRequest.GET(sergioLocation))

        then:
        HttpClientResponseException e = thrown()
        HttpStatus.NOT_FOUND == e.status

        when: "we restart the server again"
        httpClient.close();
        embeddedServer.close();
        embeddedServer = ApplicationContext.run(EmbeddedServer.class, properties);
        httpClient = embeddedServer.getApplicationContext().createBean(HttpClient.class, embeddedServer.getURL());
        client = httpClient.toBlocking();

        and: "try to fetch Sergio"
        client.exchange(HttpRequest.GET(sergioLocation))

        then: "he is still gone"
        e = thrown(HttpClientResponseException)
        HttpStatus.NOT_FOUND == e.status

        when: "we try to get Tim"
        showResponse = client.exchange(HttpRequest.GET(timLocation), Customer)

        then:
        HttpStatus.OK == showResponse.status()

        when:
        customer = showResponse.body()

        then: "he is still there"
        customer
        timName == customer.firstName
        !customer.lastName
    }
}
