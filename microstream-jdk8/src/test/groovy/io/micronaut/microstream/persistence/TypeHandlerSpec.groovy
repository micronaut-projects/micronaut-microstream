package io.micronaut.microstream.persistence

import io.micronaut.context.ApplicationContext
import io.micronaut.context.exceptions.NoSuchBeanException
import io.micronaut.inject.qualifiers.Qualifiers
import io.micronaut.runtime.server.EmbeddedServer
import one.microstream.storage.embedded.types.EmbeddedStorageFoundation
import one.microstream.persistence.binary.jdk8.java.util.BinaryHandlerVector as Java8BinaryHandlerVector
import one.microstream.persistence.binary.java.util.BinaryHandlerVector as DefaultBinaryHandlerVector
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.TempDir

class TypeHandlerSpec extends Specification {

    @TempDir
    @Shared
    File tempDir

    void "Vector maps to #expected when jdk8 listener is enabled = #enabled"() {
        given:
        EmbeddedServer server = ApplicationContext.run(EmbeddedServer, [
                "microstream.storage.people.root-class"             : People.class.name,
                "microstream.storage.people.storage-directory"      : new File(tempDir, "people").absolutePath,
                "microstream.persistence.type-handlers.jdk8.enabled": enabled,
        ])

        when:
        def bean = server.applicationContext.getBean(EmbeddedStorageFoundation, Qualifiers.byName('people'))

        then:
        expected.isAssignableFrom(bean.connectionFoundation.customTypeHandlerRegistry.lookupTypeHandler(Vector).class)

        cleanup:
        server.stop()

        where:
        enabled | expected
        "true"  | Java8BinaryHandlerVector
        "false" | DefaultBinaryHandlerVector
    }

    void "config is absent when disabled"() {
        given:
        EmbeddedServer server = ApplicationContext.run(EmbeddedServer, [
                "microstream.storage.people.root-class"             : People.class.name,
                "microstream.storage.people.storage-directory"      : new File(tempDir, "people").absolutePath,
                "microstream.persistence.type-handlers.jdk8.enabled": 'false',
        ])

        when:
        server.applicationContext.getBean(EmbeddedStorageFoundationJDK8ConfigurationProperties)

        then:
        thrown(NoSuchBeanException)

        cleanup:
        server.stop()
    }

    void "config is present when enabled"() {
        given:
        EmbeddedServer server = ApplicationContext.run(EmbeddedServer, [
                "microstream.storage.people.root-class"             : People.class.name,
                "microstream.storage.people.storage-directory"      : new File(tempDir, "people").absolutePath,
                "microstream.persistence.type-handlers.jdk8.enabled": 'true',
        ])

        when:
        def config = server.applicationContext.getBean(EmbeddedStorageFoundationJDK8ConfigurationProperties)

        then:
        config.enabled

        cleanup:
        server.stop()
    }

    static class People {
    }
}
