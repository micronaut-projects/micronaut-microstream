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

import io.micronaut.cache.DelegatingAsyncCache;

import javax.cache.Cache;
import java.util.concurrent.ExecutorService;

/**
 * An {@link io.micronaut.cache.AsyncCache} implementation that uses a Microstream Cache instance.
 *
 * @param <K> the key type
 * @param <V> the value type
 * @since 1.0.0
 * @author Tim Yates
 */
public class MicrostreamAsyncCache<K, V> extends DelegatingAsyncCache<Cache<K, V>> implements AutoCloseable {

    private final String name;

    public MicrostreamAsyncCache(String name, MicrostreamSyncCache<K, V> syncCache, ExecutorService executorService) {
        super(syncCache, executorService);
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void close() throws Exception {
        super.getNativeCache().close();
    }
}
