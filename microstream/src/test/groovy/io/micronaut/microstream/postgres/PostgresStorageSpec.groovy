package io.micronaut.microstream.postgres

import io.micronaut.context.BeanContext
import io.micronaut.context.annotation.Property
import io.micronaut.core.util.StringUtils
import io.micronaut.inject.qualifiers.Qualifiers
import io.micronaut.microstream.BaseStorageSpec
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import one.microstream.storage.embedded.types.EmbeddedStorageFoundation
import one.microstream.storage.types.StorageManager

@MicronautTest
@Property(name = "microstream.postgres.storage.foo.table-name", value = PostgresStorageSpec.TABLE_NAME)
@Property(name = "microstream.postgres.storage.foo.root-class", value = 'io.micronaut.microstream.BaseStorageSpec$Root')
@Property(name = "datasources.foo.db-type", value = "postgresql")
@Property(name = "micronaut.metrics.enabled", value = StringUtils.FALSE) // Workaround for cyclic bean creation HikariDataSource -> MetricsRegistry -> HikariDataSource
@Property(name = "spec.type", value = "storage")
class PostgresStorageSpec extends BaseStorageSpec {

    static final String TABLE_NAME = "microstreamfoo"

    @Inject
    BeanContext beanContext

    @Inject
    CustomerRepository customerRepository

    void "expected beans are created"() {
        expect:
        beanContext.containsBean(PostgresStorageConfigurationProvider, Qualifiers.byName("foo"))
        beanContext.containsBean(EmbeddedStorageFoundation, Qualifiers.byName("foo"))
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
