package io.micronaut.microstream.dynamodb

import io.micronaut.context.annotation.Requires
import io.micronaut.context.annotation.Value
import io.micronaut.context.event.BeanCreatedEvent
import io.micronaut.context.event.BeanCreatedEventListener
import jakarta.inject.Singleton
import software.amazon.awssdk.auth.credentials.AwsCredentials
import software.amazon.awssdk.services.dynamodb.DynamoDbClientBuilder

@Requires(property = "dynamodb-local.host")
@Requires(property = "dynamodb-local.port")
@Singleton
class DynamoDbClientBuilderListener
        implements BeanCreatedEventListener<DynamoDbClientBuilder> {

    private final URI endpoint
    private final String accessKeyId
    private final String secretAccessKey

    DynamoDbClientBuilderListener(@Value('${dynamodb-local.host}') String host,
                                  @Value('${dynamodb-local.port}') String port) {
        this.endpoint = "http://${host}:${port}".toURI()
        this.accessKeyId = "fakeMyKeyId"
        this.secretAccessKey = "fakeSecretAccessKey"
    }

    @Override
    DynamoDbClientBuilder onCreated(BeanCreatedEvent<DynamoDbClientBuilder> event) {
        event.getBean().endpointOverride(endpoint)
                .credentialsProvider(() -> new AwsCredentials() {
                    @Override
                    String accessKeyId() {
                        return accessKeyId;
                    }

                    @Override
                    String secretAccessKey() {
                        return secretAccessKey;
                    }
                })
    }
}
