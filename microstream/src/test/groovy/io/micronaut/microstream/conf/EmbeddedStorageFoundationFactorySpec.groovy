package io.micronaut.microstream.conf

import io.micronaut.context.BeanContext
import io.micronaut.context.annotation.Property
import io.micronaut.core.util.StringUtils
import io.micronaut.inject.qualifiers.Qualifiers
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import io.micronaut.test.support.TestPropertyProvider
import jakarta.inject.Inject
import one.microstream.storage.embedded.types.EmbeddedStorageFoundation
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.TempDir

@MicronautTest(startApplication = false)
class EmbeddedStorageFoundationFactorySpec extends Specification implements TestPropertyProvider {

    @Inject
    BeanContext beanContext

    @TempDir
    @Shared
    File tempDir

    @Override
    Map<String, String> getProperties() {
        [
                "microstream.storage.orange.storage-directory": new File(tempDir, "orange").absolutePath,
                "microstream.storage.blue.storage-directory" : new File(tempDir, "blue").absolutePath,
        ]
    }

    void "you can have multiple beans of type EmbeddedStorageFoundation"() {
        expect:
        beanContext.getBeansOfType(EmbeddedStorageFoundation).size() == 2

        when:
        beanContext.getBean(EmbeddedStorageFoundation,
                Qualifiers.byName("orange"))
        then:
        noExceptionThrown()

        when:
        beanContext.getBean(EmbeddedStorageFoundation, Qualifiers.byName("blue"))
        then:
        noExceptionThrown()
    }
}
