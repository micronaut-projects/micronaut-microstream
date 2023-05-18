/*
 * Copyright 2017-2022 original authors
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
import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.EachBean;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Parameter;
import io.micronaut.context.exceptions.DisabledBeanException;
import io.micronaut.core.reflect.InstantiationUtils;
import io.micronaut.inject.qualifiers.Qualifiers;
import io.micronaut.microstream.conf.EmbeddedStorageConfigurationProvider;
import jakarta.inject.Singleton;
import one.microstream.afs.aws.s3.types.S3Connector;
import one.microstream.afs.blobstore.types.BlobStoreFileSystem;
import one.microstream.storage.embedded.types.EmbeddedStorage;
import one.microstream.storage.embedded.types.EmbeddedStorageManager;
import one.microstream.storage.types.StorageManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.s3.S3Client;

/**
 * @author Sergio del Amo
 * @since 1.0.0
 */
@Factory
public class StorageManagerFactory {
    private static final Logger LOG = LoggerFactory.getLogger(StorageManagerFactory.class);

    private final BeanContext beanContext;

    /**
     * Constructor.
     * @param beanContext Bean Context.
     */
    public StorageManagerFactory(BeanContext beanContext) {
        this.beanContext = beanContext;
    }

    /**
     *
     * @param foundation EmbeddedStorageFoundation
     * @param name Name qualifier
     * @return EmbeddedStorageManager
     */
    @EachBean(S3StorageConfigurationProvider.class)
    @Bean(preDestroy = "shutdown")
    @Singleton
    public StorageManager createStorageManager(S3StorageConfigurationProvider provider, @Parameter String name) {
        S3Client client = S3Client.builder().build();
        BlobStoreFileSystem fileSystem = BlobStoreFileSystem.New(S3Connector.Caching(client));
        @SuppressWarnings("resource") // We don't want to close the storage manager
        EmbeddedStorageManager storageManager = EmbeddedStorage.start(fileSystem.ensureDirectoryPath("microstream"));
        storageManager.close();
        storageManager.start();
        if (storageManager.root() == null) {
            if (LOG.isTraceEnabled()) {
                LOG.trace("No data found");
            }

            if (!beanContext.containsBean(io.micronaut.microstream.conf.EmbeddedStorageConfigurationProvider.class, Qualifiers.byName(name))) {
                throw new DisabledBeanException("Please, define a bean of type " + io.micronaut.microstream.conf.EmbeddedStorageConfigurationProvider.class.getSimpleName() + " by name qualifier: " + name);
            }
            io.micronaut.microstream.conf.EmbeddedStorageConfigurationProvider configuration = beanContext.getBean(EmbeddedStorageConfigurationProvider.class, Qualifiers.byName(name));
            if (configuration.getRootClass() != null) {
                storageManager.setRoot(InstantiationUtils.instantiate(configuration.getRootClass()));
            }
            storageManager.storeRoot();
        }
        return storageManager;
    }
}
