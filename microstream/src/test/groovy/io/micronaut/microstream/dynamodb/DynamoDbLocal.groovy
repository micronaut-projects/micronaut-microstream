package io.micronaut.microstream.dynamodb

import org.testcontainers.containers.GenericContainer

class DynamoDbLocal implements AutoCloseable {

    private static GenericContainer dynamoDBLocal

    private static GenericContainer getDynamoDBLocal() {
        if (dynamoDBLocal == null) {
            dynamoDBLocal = new GenericContainer("amazon/dynamodb-local")
                    .withExposedPorts(8000)
            dynamoDBLocal.start()
        }
        dynamoDBLocal
    }

    static Map<String, String> getProperties() {
        [
                "dynamodb-local.host":  "localhost",
                "dynamodb-local.port": getDynamoDBLocal().firstMappedPort
        ] as Map<String, String>
    }

    static void shutdown() {
        dynamoDBLocal?.close()
    }

    @Override
    void close() throws Exception {
        shutdown()
    }
}
