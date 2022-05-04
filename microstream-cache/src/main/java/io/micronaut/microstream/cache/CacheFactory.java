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

import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.EachBean;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Parameter;
import io.micronaut.core.convert.ConversionService;
import io.micronaut.scheduling.TaskExecutors;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import one.microstream.cache.types.CacheConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.cache.CacheManager;
import javax.cache.Caching;
import java.util.concurrent.ExecutorService;

/**
 * @author Tim Yates
 * @since 1.0.0
 */
@Factory
public class CacheFactory {

    private static final Logger LOG = LoggerFactory.getLogger(CacheFactory.class);
    private static final String MICROSTREAM_CACHING_PROVIDER = "one.microstream.cache.types.CachingProvider";
    private final CacheManager manager;
    private final ConversionService<?> conversionService;

    /**
     * Constructor.
     *
     * @param conversionService The conversion service
     */
    public CacheFactory(ConversionService<?> conversionService) {
        this.conversionService = conversionService;
        this.manager = Caching.getCachingProvider(MICROSTREAM_CACHING_PROVIDER).getCacheManager();
    }

    /**
     * Create a cache for each CacheConfiguration.
     *
     * @param name The name of the cache
     * @param cacheConfigurationBuilder Cache Configuration Builder
     * @param <K> The key type
     * @param <V> The key type
     * @param executorService The IO executor service for async caches
     * @return The cache
     */
    @Singleton
    @Bean(preDestroy = "close")
    @EachBean(CacheConfiguration.Builder.class)
    public <K, V> MicrostreamSyncCache<K, V> createCache(
        @Parameter String name,
        CacheConfiguration.Builder<K, V> cacheConfigurationBuilder,
        @Named(TaskExecutors.IO) ExecutorService executorService
    ) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Creating cache: {}", name);
        }
        return new MicrostreamSyncCache<>(name, manager.createCache(name, cacheConfigurationBuilder.build()), conversionService, executorService);
    }
}
