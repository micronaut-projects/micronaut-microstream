package io.micronaut.microstream.health

import groovy.json.JsonSlurper
import io.micronaut.context.ApplicationContext
import io.micronaut.http.client.HttpClient
import io.micronaut.runtime.server.EmbeddedServer
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.TempDir

class MicrostreamHealthIndicatorDetailSpec extends Specification {

    @TempDir
    @Shared
    File tempDir

    void "details are shown for the health endpoint if requested"() {
        given:
        EmbeddedServer server = ApplicationContext.run(EmbeddedServer, [
                "microstream.storage.blue.storage-directory": tempDir.absolutePath,
                "endpoints.health.details-visible": "ANONYMOUS",
        ])
        def client = server.applicationContext.createBean(HttpClient, server.URL).toBlocking()

        when:
        def result = new JsonSlurper().parseText(client.retrieve("/health"))

        then:
        with(result.details.'microstream.blue') {
            status == "UP"
            details == [startingUp:false, running:true, active:true, acceptingTasks:true, shuttingDown:false, shutdown:false]
        }

        cleanup:
        client?.close()
        server?.close()
    }
}
