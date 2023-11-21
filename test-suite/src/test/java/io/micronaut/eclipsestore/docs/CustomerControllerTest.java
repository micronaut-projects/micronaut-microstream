package io.micronaut.eclipsestore.docs;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Map;
import java.util.UUID;

class CustomerControllerTest extends BaseCustomerControllerTest {

    @Override
    protected Map<String, Object> extraProperties() {
        String storageDirectory = "build/eclipsestore-" + UUID.randomUUID();
        return Map.of(
            "eclipsestore.storage.main.root-class", "io.micronaut.eclipsestore.docs.Data",
            "eclipsestore.storage.main.storage-directory", storageDirectory
        );
    }
    @ParameterizedTest
    @MethodSource("provideCustomerRepositoryImplementations")
    void testCrud(String customerRepositoryImplementation) throws Exception {
        verifyCrudWithEclipseStore(customerRepositoryImplementation);
    }
}
