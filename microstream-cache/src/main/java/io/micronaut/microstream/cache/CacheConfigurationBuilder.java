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

import io.micronaut.core.annotation.Internal;
import io.micronaut.core.util.StringUtils;
import one.microstream.cache.types.CacheConfigurationPropertyNames;
import one.microstream.collections.types.XGettingCollection;
import one.microstream.configuration.types.Configuration;
import one.microstream.configuration.types.ConfigurationValueMapperProvider;
import one.microstream.typing.KeyValue;

/**
 * A helper class to load the <a href="https://docs.microstream.one/manual/storage/configuration/properties.html">cache properties</a> from Micronaut configuration.
 *
 * @author Tim Yates
 * @since 1.0.0
 */
@Internal
class CacheConfigurationBuilder implements Configuration.Builder {

    public static final String BACKING_STORAGE = "backing-storage";

    private final Configuration.Builder delegate;

    public CacheConfigurationBuilder() {
        this.delegate = Configuration.Builder();
    }

    public Configuration.Builder setBackingStorage(String storage) {
        return delegate.set(BACKING_STORAGE, storage);
    }

    public Configuration.Builder setStatisticsEnabled(boolean statisticsEnabled) {
        return delegate.set(CacheConfigurationPropertyNames.STATISTICS_ENABLED, statisticsEnabled ? StringUtils.TRUE : StringUtils.FALSE);
    }

    public Configuration.Builder setManagementEnabled(boolean managementEnabled) {
        return delegate.set(CacheConfigurationPropertyNames.MANAGEMENT_ENABLED, managementEnabled ? StringUtils.TRUE : StringUtils.FALSE);
    }

    @Override
    public Configuration.Builder valueMapperProvider(ConfigurationValueMapperProvider valueMapperProvider) {
        return delegate.valueMapperProvider(valueMapperProvider);
    }

    @Override
    public Configuration.Builder set(String key, String value) {
        return delegate.set(key, value);
    }

    @Override
    public Configuration.Builder setAll(XGettingCollection<KeyValue<String, String>> properties) {
        return delegate.setAll(properties);
    }

    @Override
    public Configuration.Builder setAll(final KeyValue<String, String>... properties) {
        return delegate.setAll(properties);
    }

    @Override
    public Configuration.Builder child(String key) {
        return delegate.child(key);
    }

    @Override
    public Configuration buildConfiguration() {
        return delegate.buildConfiguration();
    }
}
