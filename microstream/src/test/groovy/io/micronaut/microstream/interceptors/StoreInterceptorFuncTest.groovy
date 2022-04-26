package io.micronaut.microstream.interceptors

import io.micronaut.context.BeanContext
import io.micronaut.context.annotation.Property
import io.micronaut.context.annotation.Requires
import io.micronaut.inject.qualifiers.Qualifiers
import io.micronaut.microstream.annotations.Store
import io.micronaut.microstream.annotations.StoringStrategy
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import jakarta.inject.Singleton
import one.microstream.persistence.types.Storer
import one.microstream.storage.types.StorageManager
import spock.lang.Specification

@MicronautTest(startApplication = false)
@Property(name = "spec.name", value = "StoreInterceptorFuncTest")
class StoreInterceptorFuncTest extends Specification {

    @Inject
    BeanContext beanContext

    Storer mockStorer = Mock()

    StorageManager mockStorageManager = Mock() {
        createEagerStorer() >> mockStorer
    }

    void "no name specified results in an exception"() {
        given:
        beanContext.registerSingleton(StorageManager, mockStorageManager, Qualifiers.byName('flowers'))
        SpecController controller = beanContext.getBean(SpecController)

        when:
        controller.eagerResultantStore('iris')

        then:
        controller.called

        and: "eager storage calls the storer directly"
        1 * mockStorer.store("iris") >> 1L

        when:
        controller.called = false
        controller.lazyResultantStore('spoon')

        then:
        controller.called

        and: "lazy storage stores on the manager"
        1 * mockStorageManager.store("spoon") >> 1L
    }

    @MockBean(bean = StorageManager, named = "flowers")
    StorageManager getEmbeddedStorageManager() {
        mockStorageManager
    }

    @Singleton
    @Requires(property = "spec.name", value = "StoreInterceptorFuncTest")
    static class SpecController {

        boolean called = false;

        @Store(name = "flowers", result = true, strategy = StoringStrategy.EAGER)
        String eagerResultantStore(String flower) {
            called = true
            return flower
        }

        @Store(name = "flowers", result = true)
        String lazyResultantStore(String flower) {
            called = true
            return flower
        }
    }
}
