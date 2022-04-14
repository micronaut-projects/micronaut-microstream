package io.micronaut.microstream.conf

import io.micronaut.context.BeanContext
import io.micronaut.context.annotation.Property
import io.micronaut.inject.qualifiers.Qualifiers
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import one.microstream.storage.embedded.types.EmbeddedStorageFoundation
import spock.lang.Specification

@Property(name = "microstream.storage.orange.storage-directory-in-user-home", value = "Documents/microstream")
@Property(name = "microstream.storage.blue.storage-directory-in-user-home", value = "Downloads/microstream")
@MicronautTest(startApplication = false)
class EmbeddedStorageFoundationFactorySpec extends Specification {

    @Inject
    BeanContext beanContext

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
