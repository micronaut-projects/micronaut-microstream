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
        EmbeddedStorageManager embeddedStorageManager = findStorageManager(cacheConfiguration)
            .orElse(null);
        CacheConfiguration.Builder<K, V> cacheConfigurationBuilder = embeddedStorageManager == null ? CacheConfiguration.Builder(cacheConfiguration.getKeyType(),
            cacheConfiguration.getValueType()) : CacheConfiguration.Builder(cacheConfiguration.getKeyType(),
            cacheConfiguration.getValueType(),
            cacheConfiguration.getName(),
            embeddedStorageManager);
        CacheConfiguration.Builder<?, ?> builder = CacheConfigurationBuilderConfigurationBased.New()
            .buildCacheConfiguration(createConfiguration(cacheConfiguration), cacheConfigurationBuilder);
        findExpiryPolicyFactory(cacheConfiguration)
            .ifPresent(expiryPolicyFactory -> builder.expiryPolicyFactory(expiryPolicyFactory.getFactory()));

        return builder.build();
    }

    @NonNull
    private Optional<EmbeddedStorageManager> findStorageManager(MicrostreamCacheConfiguration<K, V> cacheConfiguration) {
        String storageNameQualifier = cacheConfiguration.getStorage() != null ?
            cacheConfiguration.getStorage() : cacheConfiguration.getName();
        Optional<StorageManager> storageManagerOptional = getStorage(storageNameQualifier);
        if (!storageManagerOptional.isPresent()) {
            return Optional.empty();
        }
        StorageManager storageManager = storageManagerOptional.get();
        if (!(storageManager instanceof EmbeddedStorageManager)) {
            return Optional.empty();
        }
        return Optional.of((EmbeddedStorageManager) storageManager);
    }

    @NonNull
    private Configuration createConfiguration(@NonNull MicrostreamCacheConfiguration<K, V> cacheConfiguration) {
        Configuration.Builder configurationBuilder = Configuration.Builder();
        booleanString(cacheConfiguration.isReadThrough()).ifPresent(b ->
            configurationBuilder.set(CacheConfigurationPropertyNames.READ_THROUGH, b));
        booleanString(cacheConfiguration.isWriteThrough()).ifPresent(b ->
            configurationBuilder.set(CacheConfigurationPropertyNames.WRITE_THROUGH, b));
        booleanString(cacheConfiguration.isStoreByValue()).ifPresent(b ->
            configurationBuilder.set(CacheConfigurationPropertyNames.STORE_BY_VALUE, b));
        booleanString(cacheConfiguration.isStatisticsEnabled()).ifPresent(b ->
            configurationBuilder.set(CacheConfigurationPropertyNames.STATISTICS_ENABLED, b));
        booleanString(cacheConfiguration.isManagementEnabled()).ifPresent(b ->
            configurationBuilder.set(CacheConfigurationPropertyNames.MANAGEMENT_ENABLED, b));
        return configurationBuilder.buildConfiguration();
    }

    @NonNull
    private Optional<StorageManager> getStorage(@NonNull String name) {
        if (beanContext.containsBean(StorageManager.class, Qualifiers.byName(name))) {
            return Optional.of(beanContext.getBean(StorageManager.class, Qualifiers.byName(name)));
        }
        // Should we through an ConfigurationException
        return beanContext.findBean(StorageManager.class);
    }

    @NonNull
    private Optional<ExpiryPolicyFactory> findExpiryPolicyFactory(@NonNull Named named) {
        return beanContext.findBean(ExpiryPolicyFactory.class, Qualifiers.byName(named.getName()));
    }

    @NonNull
    private Optional<String> booleanString(Boolean value) {
        if (value == null) {
            return Optional.empty();
        }
        return Optional.of(value ? StringUtils.TRUE : StringUtils.FALSE);
    }
}
