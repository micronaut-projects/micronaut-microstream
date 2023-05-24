package io.micronaut.microstream.docs;

import io.micronaut.core.util.StringUtils;
import org.junit.jupiter.api.condition.EnabledIf;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.testcontainers.DockerClientFactory;

import java.util.Map;
import java.util.UUID;

class PostgresCustomerControllerTest extends BaseCustomerControllerTest {

    @Override
    protected Map<String, Object> extraProperties() {
        return Map.of(
            "datasources.main.db-type", "postgresql",
            "micronaut.metrics.enabled", StringUtils.FALSE,
            "microstream.postgres.storage.main.table-name", "microstream" + UUID.randomUUID(),
            "microstream.postgres.storage.main.root-class", "io.micronaut.microstream.docs.Data"
        );
    }

    @EnabledIf("dockerAvailable")
    @ParameterizedTest
    @MethodSource("provideCustomerRepositoryImplementations")
    void testCrud(String customerRepositoryImplementation) throws Exception {
        super.verifyCrudWithMicroStream(customerRepositoryImplementation);
    }
}
