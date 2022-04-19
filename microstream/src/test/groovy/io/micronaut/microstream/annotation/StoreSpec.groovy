package io.micronaut.microstream.annotation

import io.micronaut.context.BeanContext
import io.micronaut.context.annotation.Property
import io.micronaut.context.annotation.Requires
import io.micronaut.context.exceptions.NoSuchBeanException
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import io.micronaut.test.support.TestPropertyProvider
import jakarta.inject.Inject
import jakarta.inject.Named
import jakarta.inject.Singleton
import one.microstream.storage.embedded.types.EmbeddedStorageManager
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.TempDir

@MicronautTest(startApplication = false)
@Property(name = "spec.name", value = "StoreSpec")
class StoreSpec extends Specification implements TestPropertyProvider {

    @TempDir
    @Shared
    File tempDir

    @Inject
    BeanContext beanContext

    @Override
    Map<String, String> getProperties() {
        [
                "microstream.storage.orange.storage-directory-in-user-home": tempDir.absolutePath,
                "microstream.storage.blue.storage-directory-in-user-home": tempDir.absolutePath,
        ]
    }

    def "works with happy path"() {
        when:
        def result = beanContext.getBean(SpecController).store('iris')

        then:
        result
    }

    def "no name specified results in an exception"() {
        when:
        beanContext.getBean(SpecController).noNameDefined('iris')

        then:
        def e = thrown(IllegalStateException)
        e.message == StoreInterceptor.MULTIPLE_MANAGERS_WITH_NO_QUALIFIER_MESSAGE
    }

    def "bad name results in an exception"() {
        when:
        beanContext.getBean(SpecController).badNameDefined('iris')

        then:
        def e = thrown(NoSuchBeanException)
        e.message.startsWith "No bean of type [one.microstream.storage.embedded.types.EmbeddedStorageManager] exists for the given qualifier: @Named('nope')."
    }

    @Singleton
    @Requires(property = "spec.name", value = "StoreSpec")
    static class SpecController {

        private EmbeddedStorageManager manager

        public SpecController(@Named('blue') EmbeddedStorageManager manager) {
            this.manager = manager
        }

        @Store(name = 'blue')
        def store(String flower) {
            manager.store([flower])
        }

        @Store
        def noNameDefined(String flower) {
            manager.store([flower])
        }

        @Store(name = 'nope')
        def badNameDefined(String flower) {
            manager.store([flower])
        }
    }
}
