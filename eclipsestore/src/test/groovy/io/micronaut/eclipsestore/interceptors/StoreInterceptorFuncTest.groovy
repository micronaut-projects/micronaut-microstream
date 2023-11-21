package io.micronaut.eclipsestore.interceptors

import io.micronaut.context.ApplicationContext
import io.micronaut.context.annotation.Requires
import io.micronaut.inject.qualifiers.Qualifiers
import io.micronaut.eclipsestore.annotations.Store
import io.micronaut.eclipsestore.annotations.StoringStrategy
import jakarta.inject.Named
import jakarta.inject.Singleton
import org.eclipse.serializer.persistence.types.Storer
import org.eclipse.store.storage.types.StorageManager
import spock.lang.Specification

class StoreInterceptorFuncTest extends Specification {

    List<String> data = []

    Storer mockStorer = Mock()

    StorageManager mockStorageManager = Mock() {
        root() >> data
        createEagerStorer() >> mockStorer
    }

    void "no name specified results in an exception"() {
        given:
        def ctx = ApplicationContext.run([
                'spec.name': 'StoreInterceptorFuncTest'
        ])
        ctx.registerSingleton(StorageManager, mockStorageManager, Qualifiers.byName('flowers'))
        SpecController controller = ctx.getBean(SpecController)

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

        cleanup:
        ctx.close()
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
