package io.micronaut.microstream.docs;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Map;
import java.util.UUID;

class CustomerControllerTest extends BaseCustomerControllerTest {

    @Override
    protected Map<String, Object> extraProperties() {
        String storageDirectory = "build/microstream-" + UUID.randomUUID();
        return Map.of(
            "microstream.storage.main.root-class", "io.micronaut.microstream.docs.Data",
            "microstream.storage.main.storage-directory", storageDirectory
        );
    }
    @ParameterizedTest
    @MethodSource("provideCustomerRepositoryImplementations")
    void testCrud(String customerRepositoryImplementation) throws Exception {
        super.verifyCrudWithMicroStream(customerRepositoryImplementation);
    }
}
