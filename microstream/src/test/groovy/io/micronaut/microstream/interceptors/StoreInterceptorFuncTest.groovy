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
import jakarta.inject.Named
import jakarta.inject.Singleton
import one.microstream.persistence.types.Storer
import one.microstream.storage.types.StorageManager
import spock.lang.Specification

@MicronautTest(startApplication = false)
@Property(name = "spec.name", value = "StoreInterceptorFuncTest")
class StoreInterceptorFuncTest extends Specification {

    @Inject
    BeanContext beanContext

    List<String> data = []

    Storer mockStorer = Mock()

    StorageManager mockStorageManager = Mock() {
        root() >> data
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
        1 * mockStorer.store("iris")
        1 * mockStorer.commit()
        data == ["iris"]

        when:
        controller.called = false
        controller.lazyResultantStore('daisy')

        then:
        controller.called

        and: "lazy storage stores on the manager"
        1 * mockStorageManager.store("daisy")
        data == ["iris", "daisy"]

        when:
        controller.called = false
        controller.eagerRootStore('tulip')

        then:
        controller.called

        and: "eager root storage stores on the storer directly with the full root object"
        1 * mockStorer.store(["iris", "daisy", "tulip"])
        1 * mockStorer.commit()

        when:
        controller.called = false
        controller.lazyRootStore('rose')

        then:
        controller.called

        and: "lazy root storage stores on the manager with the full root object"
        1 * mockStorageManager.store(["iris", "daisy", "tulip", "rose"])
    }

    @MockBean(bean = StorageManager, named = "flowers")
    StorageManager getEmbeddedStorageManager() {
        mockStorageManager
    }

    @Singleton
    @Requires(property = "spec.name", value = "StoreInterceptorFuncTest")
    static class SpecController {

        StorageManager storageManager
        boolean called = false;

        SpecController(@Named("flowers") StorageManager storageManager) {
            this.storageManager = storageManager
        }

        @Store(name = "flowers", result = true, strategy = StoringStrategy.EAGER)
        String eagerResultantStore(String flower) {
            called = true
            storageManager.root() << flower
            return flower
        }

        @Store(name = "flowers", result = true)
        String lazyResultantStore(String flower) {
            called = true
            storageManager.root() << flower
            return flower
        }

        @Store(name = "flowers", root = true, strategy = StoringStrategy.EAGER)
        void eagerRootStore(String flower) {
            called = true
            storageManager.root() << flower
        }

        @Store(name = "flowers", root = true)
        void lazyRootStore(String flower) {
            called = true
            storageManager.root() << flower
        }
    }
}
