package io.micronaut.eclipsestore.testutils;

import org.testcontainers.containers.GenericContainer;

import java.util.Map;

public class MinioLocal extends GenericContainer<MinioLocal> {

    public MinioLocal() {
        super("minio/minio:latest");
        this.withExposedPorts(9000);
        this.withEnv(Map.of(
            "MINIO_ACCESS_KEY", "access",
            "MINIO_SECRET_KEY", "secretkey",
            "MINIO_DOMAIN", "localhost"
        ));
        this.withCommand("minio", "server", "/data");
    }

    public Map<String, Object> getProperties() {
        this.start();
        return Map.of(
            "aws.access-key-id", "access",
            "aws.secret-key", "secretkey",
            "aws.region", "us-east-1",
            "aws.services.s3.endpoint-override", "http://" + this.getHost() + ":" + getFirstMappedPort() + "/"
        );
    }
}
