package io.micronaut.microstream.dynamodb

import io.micronaut.context.BeanContext
import io.micronaut.context.annotation.Property
import io.micronaut.core.annotation.NonNull
import io.micronaut.core.util.StringUtils
import io.micronaut.inject.qualifiers.Qualifiers
import io.micronaut.microstream.BaseStorageSpec
import io.micronaut.microstream.testutils.DynamoDbLocal
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import io.micronaut.test.support.TestPropertyProvider
import jakarta.inject.Inject
import one.microstream.storage.embedded.types.EmbeddedStorageFoundation
import one.microstream.storage.types.StorageManager
import org.testcontainers.DockerClientFactory
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import spock.lang.AutoCleanup
import spock.lang.Requires
import spock.lang.Shared

@Requires({ DockerClientFactory.instance().isDockerAvailable() })
@Property(name = "micronaut.metrics.enabled", value = StringUtils.FALSE)
@Property(name = "microstream.dynamodb.storage.foobar.table-name", value = "foobartable")
@Property(name = "microstream.dynamodb.storage.foobar.root-class", value = 'io.micronaut.microstream.BaseStorageSpec$Root')
@Property(name = "spec.type", value = "storage")
@Property(name = "spec.name", value = "DynamoDbStorageSpec")
@MicronautTest
class DynamoDbStorageSpec extends BaseStorageSpec implements TestPropertyProvider {

    @Shared
    @AutoCleanup
    DynamoDbLocal dynamoDbLocal = new DynamoDbLocal()

    @Inject
    BeanContext beanContext

    @Inject
    CustomerRepository customerRepository

    @NonNull
    @Override
    Map<String, String> getProperties() {
        return dynamoDbLocal.getProperties();
    }

    void "expected beans are created"() {
        expect:
        beanContext.containsBean(DynamoDbStorageConfigurationProvider, Qualifiers.byName("foobar"))
        !beanContext.containsBean(DynamoDbClient, Qualifiers.byName("foobar"))
        beanContext.containsBean(DynamoDbClient)
        beanContext.containsBean(EmbeddedStorageFoundation, Qualifiers.byName("foobar"))
        beanContext.getBeansOfType(StorageManager).size() == 1

        when:
        customerRepository.updateName("foo")

        then:
        customerRepository.name() == "foo"

        when:
        beanContext.getBean(CustomerRepository)

        then:
        noExceptionThrown()
    }
}
