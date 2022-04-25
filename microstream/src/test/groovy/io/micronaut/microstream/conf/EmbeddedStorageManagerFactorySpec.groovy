package io.micronaut.microstream.conf

import io.micronaut.context.BeanContext
import io.micronaut.core.annotation.Introspected
import io.micronaut.inject.qualifiers.Qualifiers
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import io.micronaut.test.support.TestPropertyProvider
import jakarta.inject.Inject
import one.microstream.storage.embedded.types.EmbeddedStorageManager
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.TempDir

@MicronautTest(startApplication = false)
class EmbeddedStorageManagerFactorySpec extends Specification implements TestPropertyProvider {

    @Inject
    BeanContext beanContext

    @TempDir
    @Shared
    File tempDir

    @Override
    Map<String, String> getProperties() {
        [
                "microstream.storage.orange.root-class": BlueFlowers.class.name,
                "microstream.storage.orange.storage-directory": new File(tempDir, "orange").absolutePath,
                "microstream.storage.blue.storage-directory" : new File(tempDir, "blue").absolutePath,
                "microstream.storage.blue.root-class": BlueFlowers.class.name,
        ]
    }

    void "you can have multiple beans of type EmbeddedStorageManager"() {
        expect:
        beanContext.getBeansOfType(EmbeddedStorageManager).size() == 2

        when:
        beanContext.getBean(EmbeddedStorageManager, Qualifiers.byName("blue"))
        then:
        noExceptionThrown()
    }

    @Introspected
    static class BlueFlowers {
        List<String> flowers = []
    }
}
