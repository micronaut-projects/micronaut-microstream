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

import io.micronaut.context.annotation.EachProperty;
import io.micronaut.context.annotation.Parameter;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import one.microstream.cache.types.CacheConfiguration;

import javax.cache.configuration.CompleteConfiguration;
import javax.cache.configuration.Configuration;

/**
 * @author Sergio del Amo
 * @since 1.0.0
 */
@EachProperty("microstream.cache")
public class CacheConfigurationProperties<K, V> implements MicrostreamCacheConfiguration<K, V> {

    @Nullable
    private Class<K> keyType;

    @Nullable
    private Class<V> valueType;

    @Nullable
    private String storage;

    private boolean readThrough;

    private boolean writeThrough;

    private boolean storeByValue;

    private boolean statisticsEnabled;

    private boolean managementEnabled;

    private final String name;

    public CacheConfigurationProperties(@Parameter String name) {
        this.name = name;
    }

    @Override
    @NonNull
    public Class<K> getKeyType() {
        return keyType != null ? keyType : (Class<K>) Object.class;
    }

    @Override
    @NonNull
    public Class<V> getValueType() {
        return valueType != null ? valueType : (Class<V>) Object.class;
    }

    @Nullable
    public String getStorage() {
        return storage;
    }

    @Override
    public boolean isReadThrough() {
        return readThrough;
    }

    @Override
    public boolean isWriteThrough() {
        return writeThrough;
    }

    @Override
    public boolean isStoreByValue() {
        return storeByValue;
    }

    @Override
    public boolean isStatisticsEnabled() {
        return statisticsEnabled;
    }

    @Override
    public boolean isManagementEnabled() {
        return managementEnabled;
    }

    @Override
    @NonNull
    public String getName() {
        return name;
    }

    /**
     *
     * @param keyType The required type of keys for the Cache.
     */
    public void setKeyType(@Nullable Class<K> keyType) {
        this.keyType = keyType;
    }

    /**
     *
     * @param valueType Determines type of values for the Cache.
     */
    public void setValueType(@Nullable Class<V> valueType) {
        this.valueType = valueType;
    }

    /**
     *
     * @param storage Name qualifer for Microstream Storage Manager
     */
    public void setStorage(@Nullable String storage) {
        this.storage = storage;
    }

    /**
     * When in "read-through" mode, cache misses that occur due to cache entries not existing as a result of performing a "get" will appropriately cause the configured CacheLoader to be invoked.
     * @param readThrough Whether to use "read-through" mode
     */
    public void setReadThrough(boolean readThrough) {
        this.readThrough = readThrough;
    }

    /**
     * When in "write-through" mode, cache updates that occur as a result of performing "put" operations will appropriately cause the configured CacheWriter to be invoked.
     * @param writeThrough Whether to use "write-through" mode.
     */
    public void setWriteThrough(boolean writeThrough) {
        this.writeThrough = writeThrough;
    }

    /**
     * When a cache is storeByValue, any mutation to the key or value does not affect the key of value stored in the cache.
     * @param storeByValue When a cache is storeByValue, any mutation to the key or value does not affect the key of value stored in the cache.
     */
    public void setStoreByValue(boolean storeByValue) {
        this.storeByValue = storeByValue;
    }

    /**
     * Whether statistics collection is enabled in this cache.
     * @param statisticsEnabled Whether statistics collection is enabled in this cache.
     */
    public void setStatisticsEnabled(boolean statisticsEnabled) {
        this.statisticsEnabled = statisticsEnabled;
    }

    /**
     * Whether management is enabled on this cache.
     * @param managementEnabled Whether management is enabled on this cache.
     */
    public void setManagementEnabled(boolean managementEnabled) {
        this.managementEnabled = managementEnabled;
    }
}
