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

import io.micronaut.context.annotation.ConfigurationBuilder;
import io.micronaut.context.annotation.EachProperty;
import io.micronaut.context.annotation.Parameter;
import io.micronaut.core.annotation.NonNull;
import one.microstream.cache.types.CacheConfiguration;
import one.microstream.cache.types.CacheConfigurationBuilderConfigurationBased;

/**
 * @author Tim Yates
 * @since 1.0.0
 */
@EachProperty("microstream.cache")
public class DefaultCacheConfigurationProvider implements CacheConfigurationProvider {
    @ConfigurationBuilder
    CacheConfigurationBuilder builder = new CacheConfigurationBuilder();

    private final String name;

    public DefaultCacheConfigurationProvider(@Parameter String name) {
        this.name = name;
    }

    @Override
    @NonNull
    public CacheConfiguration.Builder<?,?> getBuilder() {
        return CacheConfigurationBuilderConfigurationBased.New().buildCacheConfiguration(builder.buildConfiguration());
    }

    @Override
    @NonNull
    public String getName() {
        return name;
    }
}
