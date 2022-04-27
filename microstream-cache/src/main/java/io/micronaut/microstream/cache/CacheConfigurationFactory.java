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
package io.micronaut.microstream.cache;

import io.micronaut.context.BeanContext;
import io.micronaut.context.annotation.EachBean;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.exceptions.DisabledBeanException;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.naming.Named;
import io.micronaut.core.util.StringUtils;
import io.micronaut.inject.qualifiers.Qualifiers;
import jakarta.inject.Singleton;
import one.microstream.cache.types.CacheConfiguration;
import one.microstream.cache.types.CacheConfigurationBuilderConfigurationBased;
import one.microstream.cache.types.CacheConfigurationPropertyNames;
import one.microstream.configuration.types.Configuration;
import one.microstream.storage.embedded.types.EmbeddedStorageManager;
import one.microstream.storage.types.StorageManager;
import java.util.Optional;

/**
 * @author Sergio del Amo
 * @since 1.0.0
 */
@Factory
public class CacheConfigurationFactory<K, V> {

    private final BeanContext beanContext;

    public CacheConfigurationFactory(BeanContext beanContext) {
        this.beanContext = beanContext;
    }

    @EachBean(MicrostreamCacheConfiguration.class)
    @Singleton
    public CacheConfiguration createCacheConfigurationProvider(MicrostreamCacheConfiguration<K, V> cacheConfiguration) {
        CacheConfiguration.Builder<?, ?> builder = CacheConfigurationBuilderConfigurationBased.New()
            .buildCacheConfiguration(createConfiguration(cacheConfiguration),
                CacheConfiguration.Builder(cacheConfiguration.getKeyType(),
                    cacheConfiguration.getValueType(),
                    cacheConfiguration.getName(),
                    findStorageManager(cacheConfiguration)));
        findExpiryPolicyFactory(cacheConfiguration)
            .ifPresent(expiryPolicyFactory -> builder.expiryPolicyFactory(expiryPolicyFactory.getFactory()));

        return builder.build();
    }

    @NonNull
    private EmbeddedStorageManager findStorageManager(MicrostreamCacheConfiguration<K, V> cacheConfiguration) {
        String storageNameQualifier = cacheConfiguration.getStorage() != null ?
            cacheConfiguration.getStorage() : cacheConfiguration.getName();
        Optional<StorageManager> storageManagerOptional = getStorage(storageNameQualifier);
        if (!storageManagerOptional.isPresent()) {
            throw new DisabledBeanException("Unable to find a StorageManager");
        }
        StorageManager storageManager = storageManagerOptional.get();
        if (!(storageManager instanceof EmbeddedStorageManager)) {
            throw new DisabledBeanException("StorageManager not an instance of EmbeddedStorageManager");
        }
        return (EmbeddedStorageManager) storageManager;
    }

    @NonNull
    private Configuration createConfiguration(@NonNull MicrostreamCacheConfiguration<K, V> cacheConfiguration) {
        Configuration.Builder configurationBuilder = Configuration.Builder();
        configurationBuilder.set(CacheConfigurationPropertyNames.READ_THROUGH, booleanString(cacheConfiguration.isReadThrough()));
        configurationBuilder.set(CacheConfigurationPropertyNames.WRITE_THROUGH, booleanString(cacheConfiguration.isWriteThrough()));
        configurationBuilder.set(CacheConfigurationPropertyNames.STORE_BY_VALUE, booleanString(cacheConfiguration.isStoreByValue()));
        configurationBuilder.set(CacheConfigurationPropertyNames.STATISTICS_ENABLED, booleanString(cacheConfiguration.isStatisticsEnabled()));
        configurationBuilder.set(CacheConfigurationPropertyNames.MANAGEMENT_ENABLED, booleanString(cacheConfiguration.isManagementEnabled()));
        return configurationBuilder.buildConfiguration();
    }

    @NonNull
    private Optional<StorageManager> getStorage(@NonNull String name) {
        if (beanContext.containsBean(StorageManager.class, Qualifiers.byName(name))) {
            return Optional.of(beanContext.getBean(StorageManager.class, Qualifiers.byName(name)));
        }
        return beanContext.findBean(StorageManager.class);
    }

    @NonNull
    private Optional<ExpiryPolicyFactory> findExpiryPolicyFactory(@NonNull Named named) {
        return beanContext.findBean(ExpiryPolicyFactory.class, Qualifiers.byName(named.getName()));
    }

    @NonNull
    private String booleanString(boolean value) {
        return value ? StringUtils.TRUE : StringUtils.FALSE;
    }
}
