package io.micronaut.eclipsestore.rest

import io.micronaut.context.ApplicationContext
import io.micronaut.core.annotation.NonNull
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.BlockingHttpClient
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.runtime.server.EmbeddedServer
import io.micronaut.serde.ObjectMapper
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
                "eclipsestore.storage.people.root-class": People.class.name,
                "eclipsestore.storage.people.storage-directory": new File(tempDir, "people").absolutePath,
                'eclipsestore.rest.path': 'api',
                'eclipsestore.rest.enabled': 'true',
        )
        def client = getClient(server)
        def mapper = server.applicationContext.getBean(ObjectMapper)

        when:
        client.exchange("/eclipsestore/root", String, Map)

        then:
        HttpClientResponseException e = thrown()
        e.status == HttpStatus.NOT_FOUND

        when:
        def root = mapper.readValue(client.retrieve("/api/root"), RootObject)

        then:
        root.name
        root.objectId

        when:
        def config = server.applicationContext.getBean(EclipseStoreRestControllerConfiguration)

        then:
        config.path == "api"

        then:
        cleanup:
        client.close()
        server.stop()
    }

    void "controller is disabled by default"() {
        given:
        def server = startServer(
                "eclipsestore.storage.people.root-class": People.class.name,
                "eclipsestore.storage.people.storage-directory": new File(tempDir, "people").absolutePath,
        )
        def client = getClient(server)

        when:
        client.exchange("/eclipsestore/root", String, Map)

        then:
        HttpClientResponseException e = thrown()
        e.status == HttpStatus.NOT_FOUND

        and:
        !server.applicationContext.containsBean(EclipseStoreRestControllerConfiguration)
        !server.applicationContext.containsBean(EclipseStoreRestController)
        !server.applicationContext.containsBean(EclipseStoreRestService)

        cleanup:
        client.close()
        server.stop()
    }

    void "storage name is required if multiple exist"() {
        given:
        def server = startServer(
                "eclipsestore.storage.people.root-class": People.class.name,
                "eclipsestore.storage.people.storage-directory": new File(tempDir, "people").absolutePath,
                "eclipsestore.storage.towns.root-class": Towns.class.name,
                "eclipsestore.storage.towns.storage-directory": new File(tempDir, "towns").absolutePath,
                'eclipsestore.rest.enabled': 'true',
        )
        def client = getClient(server)
        def mapper = server.applicationContext.getBean(ObjectMapper)

        when:
        client.exchange("/eclipsestore/root", String, Map)

        then:
        HttpClientResponseException e = thrown()
        e.status == HttpStatus.NOT_FOUND

        when:
        def root = mapper.readValue(client.retrieve("/eclipsestore/people/root"), RootObject)

        then:
        root.name
        root.objectId

        when:
        root = mapper.readValue(client.retrieve("/eclipsestore/towns/root"), RootObject)

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
