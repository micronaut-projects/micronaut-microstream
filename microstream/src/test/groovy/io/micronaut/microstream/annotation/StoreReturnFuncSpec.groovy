package io.micronaut.microstream.annotation

import io.micronaut.context.BeanContext
import io.micronaut.context.annotation.Property
import io.micronaut.context.annotation.Requires
import io.micronaut.microstream.conf.RootInstanceProvider
import io.micronaut.test.annotation.MockBean
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
@Property(name = "spec.name", value = "StoreReturnFuncSpec")
class StoreReturnFuncSpec extends Specification implements TestPropertyProvider {

    @TempDir
    @Shared
    File tempDir

    @Inject
    BeanContext beanContext

    EmbeddedStorageManager mockStorageManager = Mock()

    @Override
    Map<String, String> getProperties() {
        [
                "microstream.storage.orange.storage-directory": tempDir.absolutePath,
        ]
    }

    void "no name specified results in an exception"() {
        given:
        SpecController controller = beanContext.getBean(SpecController)

        when:
        controller.store('iris')

        then:
        controller.called

        and:
        1 * mockStorageManager.storeAll("iris") >> [1L]
    }

    @MockBean(bean = EmbeddedStorageManager)
    EmbeddedStorageManager getEmbeddedStorageManager() {
        mockStorageManager
    }

    @Singleton
    @Requires(property = "spec.name", value = "StoreReturnFuncSpec")
    static class SpecController {

        private EmbeddedStorageManager manager
        boolean called = false;

        SpecController(EmbeddedStorageManager manager) {
            this.manager = manager
        }

        @StoreReturn
        String store(String flower) {
            called = true
            return flower
        }
    }

    @Named("orange")
    @Requires(property = "spec.name", value = "StoreReturnFuncSpec")
    @Singleton
    static class OrangeInstanceProvider implements RootInstanceProvider<SimpleList> {

        @Override
        SimpleList rootInstance() {
            return new SimpleList()
        }
    }

    static class SimpleList {
        List<String> data = []
    }
}
