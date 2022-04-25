package io.micronaut.microstream.docs

import io.micronaut.context.BeanContext
import io.micronaut.context.annotation.Property
import io.micronaut.http.HttpHeaders
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.BlockingHttpClient
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import spock.lang.Specification
import io.micronaut.microstream.conf.RootInstanceProvider

@Property(name = "microstream.storage.main.root-class", value = "io.micronaut.microstream.docs.Data")
@Property(name = "microstream.storage.main.storage-directory", value = "build/microstream")
@MicronautTest
class CustomerControllerSpec extends Specification {
    @Inject
    @Client("/")
    HttpClient httpClient

    @Inject
    BeanContext beanContext

    void "verify CRUD with Microstream"() {
        given:
        String firstName = "Sergio"
        BlockingHttpClient client = httpClient.toBlocking()

        when:
        HttpRequest<?> request = HttpRequest.POST("/customer", Collections.singletonMap("firstName", firstName))
        HttpResponse<?> response = client.exchange(request)

        then:
        HttpStatus.CREATED == response.status()

        when:
        String location = response.getHeaders().get(HttpHeaders.LOCATION)

        then:
        location

        when:
        HttpRequest<?> showRequest = HttpRequest.GET(location)
        HttpResponse<Customer> showResponse = client.exchange(showRequest, Customer)

        then:
        HttpStatus.OK == showResponse.status()

        when:
        Customer customer = showResponse.body()

        then:
        customer
        firstName == customer.firstName
        !customer.lastName

        when:
        HttpResponse<Customer> deleteResponse = client.exchange(HttpRequest.DELETE(location), Customer)
        then:
        HttpStatus.NO_CONTENT == deleteResponse.status()

        when:
        client.exchange(showRequest)

        then:
        HttpClientResponseException e = thrown()
        HttpStatus.NOT_FOUND == e.status
    }
}
