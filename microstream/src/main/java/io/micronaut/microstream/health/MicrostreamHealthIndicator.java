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
package io.micronaut.microstream.health;

import io.micronaut.context.BeanContext;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.naming.Named;
import io.micronaut.core.util.StringUtils;
import io.micronaut.health.HealthStatus;
import io.micronaut.inject.BeanDefinition;
import io.micronaut.management.endpoint.health.HealthEndpoint;
import io.micronaut.management.health.indicator.HealthIndicator;
import io.micronaut.management.health.indicator.HealthResult;
import jakarta.inject.Singleton;
import one.microstream.storage.embedded.types.EmbeddedStorageManager;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A {@link HealthIndicator} that checks the health of all registered {@link EmbeddedStorageManager}s.
 *
 * @since 1.0.0
 * @author Tim Yates
 */
@Singleton
@Requires(classes = HealthIndicator.class)
@Requires(property = HealthEndpoint.PREFIX + "." +  MicrostreamHealthIndicator.MICROSTREAM_PREFIX + ".enabled", value = StringUtils.TRUE, defaultValue = StringUtils.TRUE)
public class MicrostreamHealthIndicator implements HealthIndicator {

    public static final String MICROSTREAM_PREFIX = "microstream";
    private static final String DOT = ".";
    private final Map<String, EmbeddedStorageManager> embeddedStorageManagerMap = new ConcurrentHashMap<>();

    public MicrostreamHealthIndicator(BeanContext beanContext) {
        for (BeanDefinition<EmbeddedStorageManager> definition : beanContext.getBeanDefinitions(EmbeddedStorageManager.class)) {
            if (definition.getDeclaredQualifier() instanceof Named) {
                String name = ((Named) definition.getDeclaredQualifier()).getName();
                EmbeddedStorageManager embeddedStorageManager = beanContext.getBean(definition);
                embeddedStorageManagerMap.putIfAbsent(name, embeddedStorageManager);
            }
        }
    }

    @Override
    public Publisher<HealthResult> getResult() {
        return Flux.fromIterable(embeddedStorageManagerMap.entrySet())
            .map(namedBean -> {
                EmbeddedStorageManager manager = namedBean.getValue();
                return HealthResult.builder(
                        String.join(DOT, MICROSTREAM_PREFIX, namedBean.getKey()),
                        namedBean.getValue().isRunning() ? HealthStatus.UP : HealthStatus.DOWN
                    ).details(new MicrostreamHealth(manager.isStartingUp(),
                        manager.isRunning(),
                        manager.isActive(),
                        manager.isAcceptingTasks(),
                        manager.isShuttingDown(),
                        manager.isShutdown()))
                    .build();
            });
    }
}
