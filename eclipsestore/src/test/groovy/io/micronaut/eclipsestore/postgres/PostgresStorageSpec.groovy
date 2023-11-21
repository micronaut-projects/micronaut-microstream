package io.micronaut.eclipsestore.postgres

import io.micronaut.context.BeanContext
import io.micronaut.context.annotation.Property
import io.micronaut.core.util.StringUtils
import io.micronaut.inject.qualifiers.Qualifiers
import io.micronaut.eclipsestore.BaseStorageSpec
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import org.eclipse.store.storage.embedded.types.EmbeddedStorageFoundation
import org.eclipse.store.storage.types.StorageManager
import spock.lang.Requires
import javax.sql.DataSource
import org.testcontainers.DockerClientFactory

@Requires({ DockerClientFactory.instance().isDockerAvailable() })
@MicronautTest
@Property(name = "eclipsestore.postgres.storage.foo.table-name", value = PostgresStorageSpec.TABLE_NAME)
@Property(name = "eclipsestore.postgres.storage.foo.root-class", value = 'io.micronaut.eclipsestore.BaseStorageSpec$Root')
@Property(name = "datasources.foo.db-type", value = "postgresql")
@Property(name = "micronaut.metrics.enabled", value = StringUtils.FALSE) // Workaround for cyclic bean creation HikariDataSource -> MetricsRegistry -> HikariDataSource
@Property(name = "spec.type", value = "storage")
class PostgresStorageSpec extends BaseStorageSpec {

    static final String TABLE_NAME = "eclipsestorefoo"

    @Inject
    BeanContext beanContext

    @Inject
    CustomerRepository customerRepository

    @Inject
    DataSource dataSource

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

        and:
        tables(dataSource).contains(TABLE_NAME)
    }

    List<String> tables(DataSource dataSource) {
        def result = []
        dataSource.connection.withCloseable {
            it.metaData.getTables(null, null, "$TABLE_NAME%", null).with {
                while(it.next()) {
                    result << it.getString(3)
                }
            }
        }
        result
    }
}
