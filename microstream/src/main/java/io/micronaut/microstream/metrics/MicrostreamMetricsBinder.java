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
package io.micronaut.microstream.metrics;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;
import io.micronaut.configuration.metrics.annotation.RequiresMetrics;
import io.micronaut.configuration.metrics.micrometer.MeterRegistryFactory;
import io.micronaut.context.BeanContext;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.naming.Named;
import io.micronaut.core.util.StringUtils;
import io.micronaut.inject.BeanDefinition;
import jakarta.inject.Singleton;
import one.microstream.storage.embedded.types.EmbeddedStorageManager;

import java.util.Collection;
import java.util.function.Supplier;

/**
 * A Micronaut {@link MeterBinder} for Microstream integration.
 *
 * @since 1.0.0
 */
@Singleton
@RequiresMetrics
@Requires(property = MeterRegistryFactory.MICRONAUT_METRICS_BINDERS + ".microstream.enabled", value = StringUtils.TRUE, defaultValue = StringUtils.TRUE)
@Requires(classes = MeterBinder.class)
public class MicrostreamMetricsBinder implements MeterBinder {

    public static final String MICROSTREAM_METRIC_PREFIX = "microstream.";
    private static final String SUFFIX_TOTAL_DATA_LENGTH = "totalDataLength";
    private static final String SUFFIX_FILE_COUNT = "fileCount";
    private static final String SUFFIX_LIVE_DATA_LENGTH = "liveDataLength";
    private static final String DESCRIPTION_TOTAL_DATA_LENGTH = "Displays total data length. This is the accumulated size of all storage data files.";
    private static final String DESCRIPTION_FILE_COUNT = "Displays the number of storage files.";
    private static final String DESCRIPTION_LIVE_DATA_LENGTH = "Displays live data length. This is the 'real' size of the stored data.";

    private final Collection<BeanDefinition<EmbeddedStorageManager>> storageManagerDefinitions;

    private final BeanContext beanContext;

    public MicrostreamMetricsBinder(BeanContext beanContext) {
        this.beanContext = beanContext;
        this.storageManagerDefinitions = beanContext.getBeanDefinitions(EmbeddedStorageManager.class);
    }

    @Override
    public void bindTo(@NonNull MeterRegistry registry) {
        for (BeanDefinition<EmbeddedStorageManager> definition : storageManagerDefinitions) {
            String name = ((Named) definition.getDeclaredQualifier()).getName();
            EmbeddedStorageManager manager = beanContext.getBean(definition);

            gauge(registry, name, SUFFIX_TOTAL_DATA_LENGTH,
                DESCRIPTION_TOTAL_DATA_LENGTH,
                () -> manager.createStorageStatistics().totalDataLength()
            );
            gauge(registry, name, SUFFIX_FILE_COUNT,
                DESCRIPTION_FILE_COUNT,
                () -> manager.createStorageStatistics().fileCount()
            );
            gauge(registry, name, SUFFIX_LIVE_DATA_LENGTH,
                DESCRIPTION_LIVE_DATA_LENGTH,
                () -> manager.createStorageStatistics().liveDataLength()
            );
        }
    }

    private void gauge(MeterRegistry registry, String managerName, String suffix, String description, Supplier<Number> value) {
        Gauge.builder(MICROSTREAM_METRIC_PREFIX + managerName + "." + suffix, value)
            .description(description)
            .register(registry);
    }
}
