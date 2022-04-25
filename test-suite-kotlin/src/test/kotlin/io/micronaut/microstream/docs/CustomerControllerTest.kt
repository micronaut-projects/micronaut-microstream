package io.micronaut.microstream.docs

import io.micronaut.context.annotation.Property
import io.micronaut.http.HttpHeaders
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable

@Property(name = "microstream.storage.main.root-class", value = "io.micronaut.microstream.docs.Data")
@Property(name = "microstream.storage.main.storage-directory", value = "build/microstream")
@MicronautTest
class CustomerControllerTest {
    @Inject
    @field:Client("/")
    lateinit var httpClient: HttpClient

    @Test
    fun verifyCrudWithMicrostream() {
        val firstName = "Sergio"
        val client = httpClient.toBlocking()
        val request: HttpRequest<Any> = HttpRequest.POST("/customer", mapOf("firstName" to firstName))
        val response: HttpResponse<Any> = client.exchange(request)
        Assertions.assertEquals(HttpStatus.CREATED, response.status())
        val location = response.headers[HttpHeaders.LOCATION]
        Assertions.assertNotNull(location)
        val showRequest: HttpRequest<Any> = HttpRequest.GET(location)
        val showResponse = client.exchange(showRequest, Customer::class.java)
        Assertions.assertEquals(HttpStatus.OK, showResponse.status())
        val customer = showResponse.body()
        Assertions.assertNotNull(customer)
        Assertions.assertEquals(firstName, customer!!.firstName)
        Assertions.assertNull(customer.lastName)
        val deleteResponse = client.exchange(
            HttpRequest.DELETE<Any>(location),
            Customer::class.java
        )
        Assertions.assertEquals(HttpStatus.NO_CONTENT, deleteResponse.status())
        val e = Executable { client.exchange(showRequest, Customer::class.java) }
        val thrown = Assertions.assertThrows(HttpClientResponseException::class.java, e)
        Assertions.assertEquals(HttpStatus.NOT_FOUND, thrown.status)
    }
}
