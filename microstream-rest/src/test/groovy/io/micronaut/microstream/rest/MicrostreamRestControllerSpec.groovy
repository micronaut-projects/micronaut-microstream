package io.micronaut.microstream.rest

import io.micronaut.context.BeanContext
import io.micronaut.context.annotation.Property
import io.micronaut.core.annotation.Introspected
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import io.micronaut.test.support.TestPropertyProvider
import jakarta.inject.Inject
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.TempDir

@MicronautTest
@Property(name = "spec.name", value = "MicrostreamRestControllerSpec")
class MicrostreamRestControllerSpec extends Specification implements TestPropertyProvider {

    @TempDir
    @Shared
    File tempDir

    @Inject
    BeanContext beanContext

    @Inject
    @Client("/")
    HttpClient httpClient

    @Override
    Map<String, String> getProperties() {
        [
                "endpoints.routes.enabled": "true",
                "endpoints.routes.sensitive": "false",
                "microstream.storage.people.root-class": People.class.name,
                "microstream.storage.people.storage-directory": new File(tempDir, "people").absolutePath,
                "microstream.storage.towns.root-class": Towns.class.name,
                "microstream.storage.towns.storage-directory" : new File(tempDir, "towns").absolutePath,
        ]
    }

    def 'works'() {
        when:
        def result = httpClient.toBlocking().retrieve("/microstream/people/root")

        then:
        result == '{"name":"ROOT","objectId":"1000000000000000028"}'
    }

    @Introspected
    static class Towns {
        List<String> towns = []
    }

    @Introspected
    static class People {
        List<String> people = []
    }
}
