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
package io.micronaut.microstream.annotation;

import io.micronaut.aop.InterceptorBean;
import io.micronaut.aop.MethodInterceptor;
import io.micronaut.aop.MethodInvocationContext;
import io.micronaut.context.BeanContext;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.naming.Named;
import io.micronaut.core.util.StringUtils;
import io.micronaut.inject.qualifiers.Qualifiers;
import jakarta.inject.Singleton;
import one.microstream.concurrency.XThreads;
import one.microstream.storage.embedded.types.EmbeddedStorageManager;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * Method interceptor for saving data to the MicroStream store.
 *
 * @since 1.0.0
 */
@Requires(beans = EmbeddedStorageManager.class)
@Singleton
@InterceptorBean(StoreAll.class)
public class StoreAllInterceptor implements MethodInterceptor<Object, Object> {

    public static final String MULTIPLE_MANAGERS_WITH_NO_QUALIFIER_MESSAGE = "Multiple storage managers found, but no name was specified.";

    private final List<String> names;
    private final BeanContext beanContext;

    private final ConcurrentHashMap<String, EmbeddedStorageManager> managerLookup = new ConcurrentHashMap<>();

    public StoreAllInterceptor(BeanContext beanContext) {
        this.beanContext = beanContext;
        this.names = beanContext.getBeanDefinitions(EmbeddedStorageManager.class)
            .stream()
            .map(definition -> ((Named) definition.getDeclaredQualifier()).getName())
            .collect(Collectors.toList());
    }

    @Override
    @SuppressWarnings("java:S2142")  // We don't need to bubble the interrupted exception
    public Object intercept(MethodInvocationContext<Object, Object> context) {
        String name = Optional.ofNullable(context.getAnnotation(StoreAll.class)).flatMap(a -> a.stringValue("name")).orElse(null);

        @SuppressWarnings("resource") // We don't want to close the storage manager
        EmbeddedStorageManager manager = lookupManager(name);

        CompletableFuture<Object> result = new CompletableFuture<>();
        XThreads.executeSynchronized(() -> {
            try {
                result.complete(context.proceed());
            } finally {
                manager.storeAll();
            }
        });

        try {
            return result.get();
        } catch (ExecutionException | InterruptedException e) {
            throw new StorageInterceptorException("Failed to wrap data storage", e);
        }
    }

    private EmbeddedStorageManager lookupManager(String name) {
        if (StringUtils.isNotEmpty(name)) {
            return managerLookup.computeIfAbsent(name, this::getManagerForName);
        } else {
            if (names.size() > 1) {
                throw new IllegalStateException(MULTIPLE_MANAGERS_WITH_NO_QUALIFIER_MESSAGE);
            } else {
                return managerLookup.computeIfAbsent(names.get(0), this::getManagerForName);
            }
        }
    }

    private EmbeddedStorageManager getManagerForName(String name) {
        return beanContext.getBean(EmbeddedStorageManager.class, Qualifiers.byName(name));
    }

}
