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

import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.naming.Named;

/**
 * Microstream Cache Configuration.
 * @author Sergio del Amo
 * @since 1.0.0
 * @param <K> Key Type
 * @param <V> Value Type
 */
public interface MicrostreamCacheConfiguration<K, V> extends Named {
    /**
     *
     * @return The required type of keys for the Cache.
     */
    @NonNull
    Class<K> getKeyType();

    /**
     *
     * @return Determines type of values for the Cache.
     */
    @NonNull
    Class<V> getValueType();

    /**
     *
     * @return Name qualifier for Storage Manager.
     */
    @Nullable
    String getStorage();

    /**
     * When in "read-through" mode, cache misses that occur due to cache entries not existing as a result of performing a "get" will appropriately cause the configured CacheLoader to be invoked.
     * @return Whether to use "read-through" mode
     */
    @Nullable
    Boolean isReadThrough();

    /**
     * When in "write-through" mode, cache updates that occur as a result of performing "put" operations will appropriately cause the configured CacheWriter to be invoked.
     * @return Whether to use "write-through" mode.
     */
    @Nullable
    Boolean isWriteThrough();

    /**
     * When a cache is storeByValue, any mutation to the key or value does not affect the key of value stored in the cache.
     * @return Whether the cache is Story by value.
     */
    @Nullable
    Boolean isStoreByValue();

    /**
     * Whether statistics collection is enabled in this cache.
     * @return Whether statistics collection is enabled in this cache.
     */
    @Nullable
    Boolean isStatisticsEnabled();

    /**
     * Whether management is enabled on this cache.
     * @return Whether management is enabled on this cache.
     */
    @Nullable
    Boolean isManagementEnabled();
}
