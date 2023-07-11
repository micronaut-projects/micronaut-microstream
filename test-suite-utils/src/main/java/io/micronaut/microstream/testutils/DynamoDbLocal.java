package io.micronaut.microstream.testutils;

import org.testcontainers.containers.GenericContainer;

import java.util.Map;

public class DynamoDbLocal extends GenericContainer<DynamoDbLocal> {

    public DynamoDbLocal() {
        super("amazon/dynamodb-local");
        this.withExposedPorts(8000);
    }

    public Map<String, Object> getProperties() {
        this.start();
        return Map.of(
            "dynamodb-local.host", "localhost",
            "dynamodb-local.port", getFirstMappedPort(),
            "aws.region", "us-east-1"
        );
    }
}
