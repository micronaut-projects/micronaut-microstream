package io.micronaut.microstream.conf

import io.micronaut.context.BeanContext
import io.micronaut.context.annotation.Property
import io.micronaut.inject.qualifiers.Qualifiers
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import spock.lang.Specification

@Property(name = "microstream.storage.orange.storage-directory-in-user-home", value = "Documents/microstream")
@Property(name = "microstream.storage.blue.storage-directory-in-user-home", value = "Downloads/microstream")
@MicronautTest(startApplication = false)
class EmbeddedStorageConfigurationProviderSpec extends Specification {

    @Inject
    BeanContext beanContext

    void "you can have multiple beans of type EmbeddedStorageConfigurationProvider"() {
        expect:
        beanContext.getBeansOfType(EmbeddedStorageConfigurationProvider).size() == 2

        when:
        EmbeddedStorageConfigurationProvider orangeProvider = beanContext.getBean(EmbeddedStorageConfigurationProvider,
                Qualifiers.byName("orange"))
        then:
        'orange' == orangeProvider.name
        orangeProvider.builder.buildConfiguration().get("storage-directory").endsWith('Documents/microstream')

        when:
        EmbeddedStorageConfigurationProvider blueProvider = beanContext.getBean(EmbeddedStorageConfigurationProvider,
                Qualifiers.byName("blue"))
        then:
        'blue' == blueProvider.name
        blueProvider.builder.buildConfiguration().get("storage-directory").endsWith('Downloads/microstream')
    }

    def cleanupSpec() {
        new File(System.getProperty('user.home'), "/Documents/microstream").deleteDir()
        new File(System.getProperty('user.home'), "/Downloads/microstream").deleteDir()
    }
}
