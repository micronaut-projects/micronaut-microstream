package io.micronaut.microstream.postgres

import io.micronaut.context.BeanContext
import io.micronaut.context.annotation.Property
import io.micronaut.core.util.StringUtils
import io.micronaut.inject.qualifiers.Qualifiers
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import jakarta.inject.Singleton
import one.microstream.storage.embedded.types.EmbeddedStorageFoundation
import one.microstream.storage.types.StorageManager
import spock.lang.Specification

@Property(name = "microstream.postgres.storage.foo.table-name", value = PostgresStorageSpec.TABLE)
@Property(name = "microstream.postgres.storage.foo.root-class", value = "io.micronaut.microstream.s3.Root")
@Property(name = "datasources.foo.db-type", value = "postgresql")
@Property(name = "micronaut.metrics.binders.jdbc.enabled", value = StringUtils.FALSE)
@MicronautTest
class PostgresStorageSpec extends Specification {

    static final TABLE = "test"

    @Inject
    BeanContext beanContext

    void "expected beans are created"() {
        expect:
        beanContext.containsBean(PostgresStorageConfigurationProvider, Qualifiers.byName("foo"))
        beanContext.containsBean(EmbeddedStorageFoundation, Qualifiers.byName("foo"))
        beanContext.getBeansOfType(StorageManager).size() == 1

        when:
        beanContext.getBean(CustomerRepository)

        then:
        noExceptionThrown()
    }

    @Singleton
    static class CustomerRepository {

        private final StorageManager storageManager

        CustomerRepository(StorageManager storageManager) {
            this.storageManager = storageManager
        }
    }
}
