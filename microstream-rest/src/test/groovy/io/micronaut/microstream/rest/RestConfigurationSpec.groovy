package io.micronaut.microstream.rest

import com.fasterxml.jackson.databind.ObjectMapper
import io.micronaut.context.ApplicationContext
import io.micronaut.core.annotation.NonNull
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.BlockingHttpClient
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.runtime.server.EmbeddedServer
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.TempDir

class RestConfigurationSpec extends Specification {

    @TempDir
    @Shared
    File tempDir

    void "prefix can be configured"() {
        given:
        def server = startServer(
                "microstream.storage.people.root-class": People.class.name,
                "microstream.storage.people.storage-directory": new File(tempDir, "people").absolutePath,
                'microstream.rest.path': 'api',
        )
        def client = getClient(server)
        def mapper = server.applicationContext.getBean(ObjectMapper)

        when:
        client.exchange("/microstream/root", String, Map)

        then:
        HttpClientResponseException e = thrown()
        e.status == HttpStatus.NOT_FOUND

        when:
        def root = mapper.readValue(client.retrieve("/api/root"), RootObject)

        then:
        root.name
        root.objectId

        cleanup:
        client.close()
        server.stop()
    }

    void "controller can be disabled"() {
        given:
        def server = startServer(
                "microstream.storage.people.root-class": People.class.name,
                "microstream.storage.people.storage-directory": new File(tempDir, "people").absolutePath,
                'microstream.rest.enabled': 'false',
        )
        def client = getClient(server)

        when:
        client.exchange("/microstream/root", String, Map)

        then:
        HttpClientResponseException e = thrown()
        e.status == HttpStatus.NOT_FOUND

        cleanup:
        client.close()
        server.stop()
    }

    void "storage name is required if multiple exist"() {
        given:
        def server = startServer(
                "microstream.storage.people.root-class": People.class.name,
                "microstream.storage.people.storage-directory": new File(tempDir, "people").absolutePath,
                "microstream.storage.towns.root-class": Towns.class.name,
                "microstream.storage.towns.storage-directory": new File(tempDir, "towns").absolutePath,
        )
        def client = getClient(server)
        def mapper = server.applicationContext.getBean(ObjectMapper)

        when:
        client.exchange("/microstream/root", String, Map)

        then:
        HttpClientResponseException e = thrown()
        e.status == HttpStatus.NOT_FOUND

        when:
        def root = mapper.readValue(client.retrieve("/microstream/people/root"), RootObject)

        then:
        root.name
        root.objectId

        when:
        root = mapper.readValue(client.retrieve("/microstream/towns/root"), RootObject)

        then:
        root.name
        root.objectId

        cleanup:
        client.close()
        server.stop()
    }

    @NonNull
    EmbeddedServer startServer(Map props = [:]) {
        ApplicationContext.run(EmbeddedServer, props)
    }

    @NonNull
    BlockingHttpClient getClient(EmbeddedServer server) {
        server.getApplicationContext().createBean(HttpClient, server.URL).toBlocking()
    }
}
