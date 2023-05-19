package io.micronaut.microstream.postgres


import io.micronaut.context.BeanContext
import io.micronaut.context.annotation.Property
import io.micronaut.core.util.StringUtils
import io.micronaut.inject.qualifiers.Qualifiers
import io.micronaut.microstream.s3.Root
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import jakarta.inject.Singleton
import one.microstream.concurrency.XThreads
import one.microstream.storage.embedded.types.EmbeddedStorageFoundation
import one.microstream.storage.types.StorageManager
import spock.lang.Specification

@Property(name = "microstream.postgres.storage.foo.table-name", value = PostgresStorageSpec.TABLE)
@Property(name = "microstream.postgres.storage.foo.root-class", value = "io.micronaut.microstream.s3.Root")
@Property(name = "datasources.foo.db-type", value = "postgresql")
@Property(name = "micronaut.metrics.enabled", value = StringUtils.FALSE) // Workaround for cyclic bean creation HikariDataSource -> MetricsRegistry -> HikariDataSource
@MicronautTest
class PostgresStorageSpec extends Specification {

    static final TABLE = "microstream"

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

    @Singleton
    static class CustomerRepository {

        private final StorageManager storageManager

        CustomerRepository(StorageManager storageManager) {
            this.storageManager = storageManager
        }

        String name() {
            return data().getName();
        }

        private void updateName(String name) {
            XThreads.executeSynchronized(() -> { // <2>
                data().setName(name)
                storageManager.storeRoot(); // <3>
            });
        }

        private Root data() {
            return (Root) storageManager.root();
        }
    }
}
