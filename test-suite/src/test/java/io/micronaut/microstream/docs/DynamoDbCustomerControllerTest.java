package io.micronaut.microstream.docs;

import io.micronaut.core.util.StringUtils;
import io.micronaut.microstream.testutils.DynamoDbLocal;
import org.junit.jupiter.api.condition.EnabledIf;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.HashMap;
import java.util.Map;

@Testcontainers
class DynamoDbCustomerControllerTest extends BaseCustomerControllerTest {

    @Container
    public final DynamoDbLocal dynamoDbLocal = new DynamoDbLocal();

    @Override
    protected Map<String, Object> extraProperties() {
        // merge two maps
        var properties = new HashMap<>(dynamoDbLocal.getProperties());
        properties.putAll(Map.of(
            "microstream.dynamodb.storage.main.table-name", "foobartable",
            "microstream.dynamodb.storage.main.root-class", "io.micronaut.microstream.docs.Data",
            "micronaut.metrics.enabled", StringUtils.FALSE
        ));
        return properties;
    }

    @EnabledIf("dockerAvailable")
    @ParameterizedTest
    @MethodSource("provideCustomerRepositoryImplementations")
    void testCrud(String customerRepositoryImplementation) throws Exception {
        super.verifyCrudWithMicroStream(customerRepositoryImplementation);
    }
}
