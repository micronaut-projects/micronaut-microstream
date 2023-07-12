package io.micronaut.microstream.dynamodb

import io.micronaut.context.BeanContext
import io.micronaut.context.annotation.Property
import io.micronaut.inject.qualifiers.Qualifiers
import io.micronaut.microstream.BaseStorageSpec
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import one.microstream.storage.embedded.types.EmbeddedStorageFoundation
import spock.lang.Specification

@Property(name = "microstream.dynamodb.storage.red.table-name", value = "redtable")
@Property(name = "microstream.dynamodb.storage.red.root-class", value = 'io.micronaut.microstream.BaseStorageSpec$Root')
@Property(name = "microstream.dynamodb.storage.blue.table-name", value = "bluetable")
@Property(name = "microstream.dynamodb.storage.blue.root-class", value = 'io.micronaut.microstream.BaseStorageSpec$Root')
@Property(name = "aws.region", value = "us-east-1")
@MicronautTest(startApplication = false)
class DynamoDbStorageConfigurationProviderSpec extends Specification {

    @Inject
    BeanContext beanContext

    void "you can have multiple beans of type DynamoDbStorageConfigurationProvider"() {

        when:
        Collection<DynamoDbStorageConfigurationProvider> dynamoDbStorageConfigurationProviderCollection = beanContext.getBeansOfType(DynamoDbStorageConfigurationProvider)

        then:
        dynamoDbStorageConfigurationProviderCollection
        dynamoDbStorageConfigurationProviderCollection.size() == 2
        dynamoDbStorageConfigurationProviderCollection.any { it.tableName == 'redtable' && it.name == 'red' && it.rootClass == BaseStorageSpec.Root && it.dynamoDbClientName.isEmpty() }
        dynamoDbStorageConfigurationProviderCollection.any { it.tableName == 'bluetable' && it.name == 'blue' && it.rootClass == BaseStorageSpec.Root && it.dynamoDbClientName.isEmpty() }

        and:
        beanContext.getBeansOfType(EmbeddedStorageFoundation).size() == 2
        beanContext.containsBean(EmbeddedStorageFoundation, Qualifiers.byName("red"))
        beanContext.containsBean(EmbeddedStorageFoundation, Qualifiers.byName("blue"))
    }
}
