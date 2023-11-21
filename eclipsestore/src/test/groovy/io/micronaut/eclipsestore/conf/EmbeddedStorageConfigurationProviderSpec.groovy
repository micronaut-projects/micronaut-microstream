package io.micronaut.eclipsestore.conf

import io.micronaut.context.BeanContext
import io.micronaut.inject.qualifiers.Qualifiers
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import io.micronaut.test.support.TestPropertyProvider
import jakarta.inject.Inject
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.TempDir

@MicronautTest(startApplication = false)
class EmbeddedStorageConfigurationProviderSpec extends Specification implements TestPropertyProvider {

    @Inject
    BeanContext beanContext

    @TempDir
    @Shared
    File tempDir

    @Override
    Map<String, String> getProperties() {
        [
                "eclipsestore.storage.orange.storage-directory": new File(tempDir, "orange").absolutePath,
                "eclipsestore.storage.blue.storage-directory" : new File(tempDir, "blue").absolutePath,
        ]
    }

    void "you can have multiple beans of type EmbeddedStorageConfigurationProvider"() {
        expect:
        beanContext.getBeansOfType(EmbeddedStorageConfigurationProvider).size() == 2

        when:
        EmbeddedStorageConfigurationProvider orangeProvider = beanContext.getBean(EmbeddedStorageConfigurationProvider,
                Qualifiers.byName("orange"))
        then:
        'orange' == orangeProvider.name
        orangeProvider.builder.buildConfiguration().get("storage-directory")

        when:
        EmbeddedStorageConfigurationProvider blueProvider = beanContext.getBean(EmbeddedStorageConfigurationProvider,
                Qualifiers.byName("blue"))
        then:
        'blue' == blueProvider.name
        blueProvider.builder.buildConfiguration().get("storage-directory")
    }
}
