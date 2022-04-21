package io.micronaut.microstream.docs

import io.micronaut.context.ApplicationContext
import io.micronaut.http.HttpHeaders
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.BlockingHttpClient
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.runtime.server.EmbeddedServer
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable

class CustomerControllerTest {

    @Test
    fun verifyCrudWithMicrostream() {
        // Given
        val (server, client) = startServer(
            mapOf("microstream.storage.one-microstream-instance.storage-directory" to "build/microstream")
        )
        val sergioName = "Sergio"
        val timName = "Tim"

        // Create sergio
        val sergioLocation = createCustomer(client, sergioName)
        assertNotNull(sergioLocation)

        // Create Tim
        val timLocation = createCustomer(client, timName)
        assertNotNull(timLocation)
        assertNotEquals(sergioLocation, timLocation)

        // Check sergio exists
        var customer = getCustomer(client, sergioLocation)
        assertEquals(sergioName, customer.firstName)
        assertNull(customer.lastName)

        // Check tim exists
        customer = getCustomer(client, timLocation)
        assertEquals(timName, customer.firstName)
        assertNull(customer.lastName)

        // Restart the server
        val (server1, client1) = startServer(
            mapOf("microstream.storage.one-microstream-instance.storage-directory" to "build/microstream"),
            server,
            client
        )

        // Sergio still exists
        customer = getCustomer(client1, sergioLocation)
        assertEquals(sergioName, customer.firstName)
        assertNull(customer.lastName)

        // Delete Sergio
        deleteCustomer(client1, sergioLocation)

        // Check sergio is gone
        var e = Executable { client1.exchange(sergioLocation, Customer::class.java) }
        var thrown = assertThrows(HttpClientResponseException::class.java, e)
        assertEquals(HttpStatus.NOT_FOUND, thrown.status)

        // Restart the server one last time
        val (server2, client2) = startServer(
            mapOf("microstream.storage.one-microstream-instance.storage-directory" to "build/microstream"),
            server1,
            client1
        )

        // Check sergio is still gone
        e = Executable { client2.exchange(sergioLocation, Customer::class.java) }
        thrown = assertThrows(HttpClientResponseException::class.java, e)
        assertEquals(HttpStatus.NOT_FOUND, thrown.status)

        // And check tim remains
        customer = getCustomer(client2, timLocation)
        assertEquals(timName, customer.firstName)
        assertNull(customer.lastName)

        // And then cleanup
        client2.close()
        server2.close()
    }

    private fun startServer(
        properties: Map<String, Any>,
        existingServer: EmbeddedServer? = null,
        existingClient: BlockingHttpClient? = null
    ): Pair<EmbeddedServer, BlockingHttpClient> {
        existingClient?.close()
        existingServer?.close()
        val server = ApplicationContext.run(EmbeddedServer::class.java, properties)
        val httpClient = server.applicationContext.createBean(HttpClient::class.java, server.url)
        return Pair(server, httpClient.toBlocking())
    }

    fun createCustomer(client: BlockingHttpClient, firstName: String): String {
        val response = client.exchange(HttpRequest.POST("/customer", mapOf("firstName" to firstName)), Any::class.java)
        assertEquals(HttpStatus.CREATED, response.status())
        return response.headers.get(HttpHeaders.LOCATION)!!
    }

    fun getCustomer(client: BlockingHttpClient, location: String): Customer {
        val showResponse = client.exchange(location, Customer::class.java)
        assertEquals(HttpStatus.OK, showResponse.status())
        return showResponse.body()!!
    }

    fun deleteCustomer(client: BlockingHttpClient, location: String) {
        val exchange = client.exchange(HttpRequest.DELETE<Customer>(location), Customer::class.java)
        assertEquals(HttpStatus.NO_CONTENT, exchange.status())
    }
}
