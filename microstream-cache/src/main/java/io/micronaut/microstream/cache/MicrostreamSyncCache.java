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

import io.micronaut.cache.AsyncCache;
import io.micronaut.cache.SyncCache;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.convert.ConversionService;
import io.micronaut.core.type.Argument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.cache.Cache;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.function.Supplier;

/**
 * A {@link SyncCache} implementation that uses a Microstream Cache instance.
 *
 * @param <K> the key type
 * @param <V> the value type
 * @since 1.0.0
 * @author Tim Yates
 */
public class MicrostreamSyncCache<K, V> implements SyncCache<Cache<K, V>>, AutoCloseable {

    private static final Logger LOG = LoggerFactory.getLogger(MicrostreamSyncCache.class);

    private final String name;
    private final Cache<K, V> cache;
    private final ConversionService<?> conversionService;
    private final ExecutorService executorService;

    public MicrostreamSyncCache(String name, Cache<K, V> cache, ConversionService<?> conversionService, ExecutorService executorService) {
        this.name = name;
        this.cache = cache;
        this.conversionService = conversionService;
        this.executorService = executorService;
    }

    @Override
    @NonNull
    public <T> Optional<T> get(@NonNull Object key, @NonNull Argument<T> requiredType) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Getting value for key {} ({})", key, requiredType);
        }
        return Optional.ofNullable(cache.get((K) key)).flatMap(v -> conversionService.convert(v, requiredType));
    }

    @Override
    @NonNull
    public <T> T get(@NonNull Object key, @NonNull Argument<T> requiredType, @NonNull Supplier<T> supplier) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Getting value for key - with supplier {} ({})", key, requiredType);
        }
        V v = cache.get((K) key);
        if (v != null) {
            return (T) v;
        }
        T t = supplier.get();
        put(key, t);

        return t;
    }

    @Override
    @NonNull
    public <T> Optional<T> putIfAbsent(@NonNull Object key, @NonNull T value) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Putting value for key {} if absent ({})", key, value);
        }
        V current = cache.get((K) key);
        cache.putIfAbsent((K) key, (V) value);
        return Optional.ofNullable((T) current);
    }

    @Override
    public void put(@NonNull Object key, @NonNull Object value) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Putting value for key {} ({})", key, value);
        }
        cache.put((K) key, (V) value);
    }

    @Override
    public void invalidate(@NonNull Object key) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Invalidating key {}", key);
        }
        cache.remove((K) key);
    }

    @Override
    public void invalidateAll() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Invalidating all");
        }
        cache.clear();
    }

    @Override
    @NonNull
    public AsyncCache<Cache<K, V>> async() {
        return new MicrostreamAsyncCache<>(name, this, executorService);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Cache<K, V> getNativeCache() {
        return cache;
    }

    @Override
    public void close() throws Exception {
        cache.close();
    }
}
