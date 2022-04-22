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
import io.micronaut.context.annotation.ConfigurationBuilder;
import io.micronaut.context.annotation.EachProperty;
import io.micronaut.context.annotation.Parameter;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.inject.qualifiers.Qualifiers;
import one.microstream.cache.types.CacheConfiguration;
import one.microstream.cache.types.CacheConfigurationBuilderConfigurationBased;

/**
 * @param <K> The key type
 * @param <V> The value type
 * @author Tim Yates
 * @since 1.0.0
 */
@EachProperty("microstream.cache")
public final class DefaultCacheConfigurationProvider<K, V> implements CacheConfigurationProvider<K, V> {
    @ConfigurationBuilder
    CacheConfigurationBuilder builder = new CacheConfigurationBuilder();

    private final String name;
    private final BeanContext beanContext;

    private Class<K> keyType;
    private Class<V> valueType;

    DefaultCacheConfigurationProvider(@Parameter String name, BeanContext beanContext) {
        this.name = name;
        this.beanContext = beanContext;
    }

    private Class<K> getKeyType() {
        return keyType != null ? keyType : (Class<K>) Object.class;
    }

    void setKeyType(Class<K> keyType) {
        this.keyType = keyType;
    }

    private Class<V> getValueType() {
        return valueType != null ? valueType : (Class<V>) Object.class;
    }

    void setValueType(Class<V> valueType) {
        this.valueType = valueType;
    }

    @Override
    @NonNull
    public CacheConfiguration.Builder<K, V> getBuilder() {
        CacheConfiguration.Builder<K, V> returnBuilder = CacheConfigurationBuilderConfigurationBased
            .New()
            .buildCacheConfiguration(
                builder.buildConfiguration(),
                CacheConfiguration.Builder(getKeyType(), getValueType())
            );

        beanContext.findBean(ExpiryPolicyFactory.class, Qualifiers.byName(name)).ifPresent(factory ->
            returnBuilder.expiryPolicyFactory(factory.getFactory())
        );

        return returnBuilder;
    }

    @Override
    @NonNull
    public String getName() {
        return name;
    }
}
