package io.micronaut.microstream.annotation

import io.micronaut.context.BeanContext
import io.micronaut.context.annotation.Property
import io.micronaut.context.annotation.Requires
import io.micronaut.inject.qualifiers.Qualifiers
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
@Property(name = "spec.name", value = "StoreAllSpec")
class StoreAllSpec extends Specification implements TestPropertyProvider {

    @TempDir
    @Shared
    File tempDir

    @Inject
    BeanContext beanContext

    @Override
    Map<String, String> getProperties() {
        [
                "microstream.storage.orange.storage-directory": tempDir.absolutePath,
                "microstream.storage.blue.storage-directory": tempDir.absolutePath,
        ]
    }

    def "works with happy path"() {
        when:
        def controller = beanContext.getBean(SpecController)

        and:
        def result = controller.store('iris')

        then:
        result

        and:
        beanContext.getBean(EmbeddedStorageManager, Qualifiers.byName("blue")).root().flowers == ['iris']
    }

    def "no name specified results in an exception"() {
        when:
        beanContext.getBean(SpecController).noNameDefined('iris')

        then:
        IllegalStateException e = thrown()
        e.message == StoreAllInterceptor.MULTIPLE_MANAGERS_WITH_NO_QUALIFIER_MESSAGE
    }

    def "bad name results in an exception"() {
        when:
        beanContext.getBean(SpecController).badNameDefined('iris')

        then:
        StorageInterceptorException e = thrown()
        e.message == 'No storage manager found for @StoreAll(name = "nope").'
    }

    @Singleton
    @Requires(property = "spec.name", value = "StoreAllSpec")
    static class SpecController {

        private EmbeddedStorageManager manager

        SpecController(@Named('blue') EmbeddedStorageManager manager) {
            this.manager = manager
        }

        @StoreAll(name = 'blue')
        boolean store(String flower) {
            manager.root().flowers.add(flower)
        }

        @StoreAll
        boolean noNameDefined(String flower) {
            manager.root().flowers.add(flower)
        }

        @StoreAll(name = 'nope')
        boolean badNameDefined(String flower) {
            manager.root().flowers.add(flower)
        }
    }
}
