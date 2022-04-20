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
package metrics;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;
import io.micronaut.configuration.metrics.annotation.RequiresMetrics;
import io.micronaut.configuration.metrics.micrometer.MeterRegistryFactory;
import io.micronaut.context.BeanContext;
import io.micronaut.context.annotation.Context;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.naming.Named;
import io.micronaut.inject.BeanDefinition;
import jakarta.inject.Singleton;
import one.microstream.storage.embedded.types.EmbeddedStorageManager;

import java.util.Collection;
import java.util.function.Supplier;

import static io.micronaut.configuration.metrics.micrometer.MeterRegistryFactory.MICRONAUT_METRICS_BINDERS;

/**
 * A Micronaut {@link MeterBinder} for Microstream integration.
 *
 * @since 1.0.0
 */
@Singleton
@Context
@RequiresMetrics
@Requires(property = MeterRegistryFactory.MICRONAUT_METRICS_BINDERS + ".microstream.enabled", value = "true", defaultValue = "true")
public class MicrostreamMetricsBinder implements MeterBinder {

    public static final String MICROSTREAM_METRIC_PREFIX = "microstream.";

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
            gauge(registry, name, "totalDataLength",
                () -> manager.createStorageStatistics().totalDataLength(),
                "Displays total data length. This is the accumulated size of all storage data files."
            );
            gauge(registry, name, "fileCount",
                () -> manager.createStorageStatistics().fileCount(),
                "Displays the number of storage files.");
            gauge(registry, name, "liveDataLength",
                () -> manager.createStorageStatistics().liveDataLength(),
                "Displays live data length. This is the 'real' size of the stored data.");
        }
    }

    private void gauge(MeterRegistry registry, String managerName, String suffix, Supplier<Number> value, String description) {
        Gauge.builder(MICROSTREAM_METRIC_PREFIX + managerName + "." + suffix, value)
            .description(description)
            .register(registry);
    }
}
