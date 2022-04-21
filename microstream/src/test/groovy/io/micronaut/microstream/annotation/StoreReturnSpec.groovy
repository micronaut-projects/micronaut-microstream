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
@Property(name = "spec.name", value = "StoreReturnSpec")
class StoreReturnSpec extends Specification implements TestPropertyProvider {

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
    @Requires(property = "spec.name", value = "StoreReturnSpec")
    static class SpecController {

        private EmbeddedStorageManager manager

        SpecController(@Named('blue') EmbeddedStorageManager manager) {
            this.manager = manager
        }

        @StoreReturn(name = 'blue')
        List<String> store(String flower) {
            manager.root().with {
                flowers.add(flower)
                flowers
            }
        }

        @StoreReturn
        List<String> noNameDefined(String flower) {
            manager.root().with {
                flowers.add(flower)
                flowers
            }
        }

        @StoreReturn(name = 'nope')
        List<String> badNameDefined(String flower) {
            manager.root().with {
                flowers.add(flower)
                flowers
            }
        }
    }
}
