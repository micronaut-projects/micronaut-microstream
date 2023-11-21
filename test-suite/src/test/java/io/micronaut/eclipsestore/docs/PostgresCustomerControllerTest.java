package io.micronaut.eclipsestore.docs;

import io.micronaut.core.util.StringUtils;
import org.junit.jupiter.api.condition.EnabledIf;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import java.util.Map;
import java.util.UUID;

class PostgresCustomerControllerTest extends BaseCustomerControllerTest {

    @Override
    protected Map<String, Object> extraProperties() {
        return Map.of(
            "datasources.main.db-type", "postgresql",
            "micronaut.metrics.enabled", StringUtils.FALSE,
            "eclipsestore.postgres.storage.main.table-name", "eclipsestore" + UUID.randomUUID(),
            "eclipsestore.postgres.storage.main.root-class", "io.micronaut.eclipsestore.docs.Data"
        );
    }

    @EnabledIf("dockerAvailable")
    @ParameterizedTest
    @MethodSource("provideCustomerRepositoryImplementations")
    void testCrud(String customerRepositoryImplementation) throws Exception {
        super.verifyCrudWithEclipseStore(customerRepositoryImplementation);
    }
}
