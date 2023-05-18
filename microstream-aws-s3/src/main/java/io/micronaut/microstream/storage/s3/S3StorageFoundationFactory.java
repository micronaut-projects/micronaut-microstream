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
package io.micronaut.microstream.storage.s3;

import io.micronaut.context.BeanContext;
import io.micronaut.context.annotation.EachBean;
import io.micronaut.context.annotation.Factory;
import io.micronaut.inject.qualifiers.Qualifiers;
import jakarta.inject.Singleton;
import one.microstream.afs.aws.s3.types.S3Connector;
import one.microstream.afs.blobstore.types.BlobStoreFileSystem;
import one.microstream.afs.types.ADirectory;
import one.microstream.storage.embedded.types.EmbeddedStorage;
import one.microstream.storage.embedded.types.EmbeddedStorageFoundation;
import software.amazon.awssdk.services.s3.S3Client;

import java.util.Optional;

@Factory
public class S3StorageFoundationFactory {

    @EachBean(S3StorageConfigurationProvider.class)
    @Singleton
    EmbeddedStorageFoundation<?> createFoundation(BeanContext ctx, S3StorageConfigurationProvider provider) {
        S3Client s3client = ctx.findBean(S3Client.class, Qualifiers.byName(provider.getName())).orElseGet(() -> ctx.getBean(S3Client.class));
        BlobStoreFileSystem fileSystem = BlobStoreFileSystem.New(
            S3Connector.Caching(s3client)
        );
        return EmbeddedStorage.Foundation(fileSystem.ensureDirectoryPath(provider.getBucketName()));
    }
}
