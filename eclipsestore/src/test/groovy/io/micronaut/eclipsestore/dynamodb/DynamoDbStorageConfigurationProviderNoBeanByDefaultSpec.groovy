package io.micronaut.eclipsestore.dynamodb

import io.micronaut.context.BeanContext
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import spock.lang.Specification

@MicronautTest(startApplication = false)
class DynamoDbStorageConfigurationProviderNoBeanByDefaultSpec extends Specification {

    @Inject
    BeanContext beanContext

    void "bean of type DynamoDbStorageConfigurationProvider does not exists by default"() {
        expect:
        !beanContext.containsBean(DynamoDbStorageConfigurationProvider)
    }
}
