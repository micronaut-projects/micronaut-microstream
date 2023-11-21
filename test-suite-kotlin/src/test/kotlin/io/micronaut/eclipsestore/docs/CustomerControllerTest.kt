package io.micronaut.eclipsestore.docs

import io.micronaut.context.ApplicationContext
import io.micronaut.http.HttpHeaders
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.BlockingHttpClient
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.runtime.server.EmbeddedServer
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

import org.junit.jupiter.api.function.Executable

class CustomerControllerTest {

    @ParameterizedTest
    @ValueSource(strings = ["embedded-storage-manager", "store"])
    fun verifyCrudWithEclipseStore(customerRepositoryImplementation: String) {
        // Given
        var server = startServer(customerRepositoryImplementation)
        val sergioName = "Sergio"
        val timName = "Tim"

        // Create sergio
        val sergioLocation = createCustomer(server.client, sergioName)
        assertNotNull(sergioLocation)

        // Create Tim
        val timLocation = createCustomer(server.client, timName)
        assertNotNull(timLocation)
        assertNotEquals(sergioLocation, timLocation)

        // Check sergio exists
        var customer = getCustomer(server.client, sergioLocation)
        assertEquals(sergioName, customer.firstName)
        assertNull(customer.lastName)

        // Check tim exists
        customer = getCustomer(server.client, timLocation)
        assertEquals(timName, customer.firstName)
        assertNull(customer.lastName)

        // Restart the server
        server = startServer(customerRepositoryImplementation, server)

        // Sergio still exists
        customer = getCustomer(server.client, sergioLocation)
        assertEquals(sergioName, customer.firstName)
        assertNull(customer.lastName)


        val sergioLastName = "del Amo"
        val patchResponse : HttpResponse<Any> = server.client.exchange(HttpRequest.PATCH(sergioLocation,
            mapOf("firstName" to customer.firstName, "lastName" to sergioLastName)))

        assertEquals(HttpStatus.OK, patchResponse.status())
        assertNotNull(patchResponse.headers.get(HttpHeaders.LOCATION))
        assertEquals(sergioLocation, patchResponse.headers.get(HttpHeaders.LOCATION))

        //when: 'When we restart the server and we re-retrieve Sergio'
        server = startServer(customerRepositoryImplementation, server)
        customer = getCustomer(server.client, sergioLocation)

        //then: "fetch Sergio, he still exists"
        assertEquals(sergioName, customer.firstName)
        assertEquals(sergioLastName, customer.lastName)

        // Delete Sergio
        deleteCustomer(server.client, sergioLocation)

        // Check sergio is gone
        var e = Executable { server.client.exchange(sergioLocation, Customer::class.java) }
        var thrown = assertThrows(HttpClientResponseException::class.java, e)
        assertEquals(HttpStatus.NOT_FOUND, thrown.status)

        // Restart the server one last time
        server = startServer(customerRepositoryImplementation, server)

        // Check sergio is still gone
        e = Executable { server.client.exchange(sergioLocation, Customer::class.java) }
        thrown = assertThrows(HttpClientResponseException::class.java, e)
        assertEquals(HttpStatus.NOT_FOUND, thrown.status)

        // And check tim remains
        customer = getCustomer(server.client, timLocation)
        assertEquals(timName, customer.firstName)
        assertNull(customer.lastName)

        // And then cleanup
        server.close()
    }

    private data class ServerAndClient(val server: EmbeddedServer, val client: BlockingHttpClient): AutoCloseable {
        override fun close() {
            server.close()
            client.close()
        }
    }

    private fun startServer(customerRepositoryImplementation: String, serverAndClient: ServerAndClient? = null): ServerAndClient {
        serverAndClient?.close()
        val config = mapOf("eclipsestore.storage.main.storage-directory" to "build/eclipsestore",
            "customer.repository" to  customerRepositoryImplementation,
            "eclipsestore.storage.main.root-class" to "io.micronaut.eclipsestore.docs.Data")
        val server = ApplicationContext.run(EmbeddedServer::class.java, config)
        val httpClient = server.applicationContext.createBean(HttpClient::class.java, server.url)
        return ServerAndClient(server, httpClient.toBlocking())
    }

    private fun createCustomer(client: BlockingHttpClient, firstName: String): String {
        val response = client.exchange(HttpRequest.POST("/customer", mapOf("firstName" to firstName)), Any::class.java)
        assertEquals(HttpStatus.CREATED, response.status())
        return response.headers.get(HttpHeaders.LOCATION)!!
    }

    private fun getCustomer(client: BlockingHttpClient, location: String): Customer {
        val showResponse = client.exchange(location, Customer::class.java)
        assertEquals(HttpStatus.OK, showResponse.status())
        return showResponse.body()!!
    }

    private fun deleteCustomer(client: BlockingHttpClient, location: String) {
        val exchange = client.exchange(HttpRequest.DELETE<Customer>(location), Customer::class.java)
        assertEquals(HttpStatus.NO_CONTENT, exchange.status())
    }
}
