package io.micronaut.microstream.conf

import io.micronaut.context.BeanContext
import io.micronaut.context.annotation.Property
import io.micronaut.inject.qualifiers.Qualifiers
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import jakarta.inject.Named
import jakarta.inject.Singleton
import one.microstream.storage.embedded.types.EmbeddedStorageManager
import spock.lang.Specification

@Property(name = "microstream.storage.orange.storage-directory-in-user-home", value = "Documents/microstream")
@Property(name = "microstream.storage.blue.storage-directory-in-user-home", value = "Downloads/microstream")
@MicronautTest(startApplication = false)
class EmbeddedStorageManagerFactorySpec extends Specification {

    @Inject
    BeanContext beanContext

    void "you can have multiple beans of type EmbeddedStorageManager"() {
        expect:
        beanContext.getBeansOfType(EmbeddedStorageManager).size() == 1

        when:
        beanContext.getBean(EmbeddedStorageManager, Qualifiers.byName("blue"))
        then:
        noExceptionThrown()
    }

    @Named("blue")
    @Singleton
    static class BlueRootInstanceProvider implements RootInstanceProvider<BlueFlowers> {

        @Override
        BlueFlowers rootInstance() {
            return new BlueFlowers();
        }
    }

    static class BlueFlowers {
        List<String> flowers = []
    }
}
