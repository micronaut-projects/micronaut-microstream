/*
 * Copyright 2017-2023 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.micronaut.microstream.s3;

import io.micronaut.context.BeanContext;
import io.micronaut.context.annotation.EachBean;
import io.micronaut.context.annotation.Factory;
import io.micronaut.inject.qualifiers.Qualifiers;
import jakarta.inject.Singleton;
import one.microstream.afs.aws.s3.types.S3Connector;
import one.microstream.afs.blobstore.types.BlobStoreFileSystem;
import one.microstream.storage.embedded.types.EmbeddedStorage;
import one.microstream.storage.embedded.types.EmbeddedStorageFoundation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.s3.S3Client;

/**
 * Factory for an S3 based EmbeddedStorageFoundation.
 *
 * @author Tim Yates
 * @since 2.0.0
 */
@Factory
public class S3StorageFoundationFactory {

    private static final Logger LOG = LoggerFactory.getLogger(S3StorageFoundationFactory.class);

    /**
     * @param ctx      Bean Context.
     * @param provider A {@link one.microstream.storage.embedded.configuration.types.EmbeddedStorageConfiguration#Builder()} provider.
     * @return A {@link one.microstream.storage.embedded.types.EmbeddedStorageFoundation}.
     */
    @Singleton
    @EachBean(S3StorageConfigurationProvider.class)
    EmbeddedStorageFoundation<?> createFoundation(
        S3StorageConfigurationProvider provider,
        BeanContext ctx
    ) {
        S3Client s3client = ctx.findBean(S3Client.class, Qualifiers.byName(provider.getName()))
            .orElseGet(() -> defaultClient(ctx));

        if (LOG.isDebugEnabled()) {
            LOG.debug("Got S3Client {}", s3client);
        }

        BlobStoreFileSystem fileSystem = BlobStoreFileSystem.New(
            S3Connector.Caching(s3client)
        );

        return EmbeddedStorage.Foundation(fileSystem.ensureDirectoryPath(provider.getBucketName()));
    }

    private S3Client defaultClient(BeanContext ctx) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("No named S3Client found. Looking for a default");
        }
        return ctx.getBean(S3Client.class);
    }
}