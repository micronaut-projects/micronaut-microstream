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
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.util.StringUtils;
import io.micronaut.inject.BeanDefinition;
import io.micronaut.inject.qualifiers.Qualifiers;
import jakarta.inject.Singleton;
import one.microstream.concurrency.XThreads;
import one.microstream.storage.embedded.types.EmbeddedStorageManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

/**
 * Method interceptor for saving data to the MicroStream store.
 *
 * @since 1.0.0
 * @author Tim Yates
 */
@Requires(beans = EmbeddedStorageManager.class)
@Singleton
@InterceptorBean(StoreReturn.class)
public class StoreReturnInterceptor implements MethodInterceptor<Object, Object> {

    public static final String MULTIPLE_MANAGERS_WITH_NO_QUALIFIER_MESSAGE = "Multiple storage managers found, but no name was specified.";

    private static final Logger LOG = LoggerFactory.getLogger(StoreReturnInterceptor.class);
    private static final String DEFAULT_SINGLE_MANAGER_KEY = "__default__";

    private final BeanContext beanContext;

    private final ConcurrentHashMap<String, EmbeddedStorageManager> managerLookup = new ConcurrentHashMap<>();

    public StoreReturnInterceptor(BeanContext beanContext) {
        this.beanContext = beanContext;
    }

    @Override
    @SuppressWarnings("java:S2142")  // We don't need to bubble the interrupted exception
    public Object intercept(MethodInvocationContext<Object, Object> context) {
        String name = Optional.ofNullable(context.getAnnotation(StoreAll.class)).flatMap(a -> a.stringValue("name")).orElse(null);

        @SuppressWarnings("resource") // We don't want to close the storage manager
        EmbeddedStorageManager manager = lookupManager(name);

        CompletableFuture<Object> result = new CompletableFuture<>();
        XThreads.executeSynchronized(() -> {
            Object returnValue = null;
            try {
                returnValue = context.proceed();
            } finally {
                if (returnValue != null) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("At {}, saving instance of {} to storage manager {}", context.getDescription(), returnValue.getClass().getName(), name);
                    }
                    manager.storeAll(returnValue);
                }
                result.complete(returnValue);
            }
        });

        try {
            return result.get();
        } catch (ExecutionException | InterruptedException e) {
            throw new StorageInterceptorException("Failed to wrap data storage", e);
        }
    }

    @NonNull
    private EmbeddedStorageManager lookupManager(@Nullable String name) {
        if (StringUtils.isNotEmpty(name)) {
            return managerLookup.computeIfAbsent(name, this::getManagerForName);
        } else {
            return managerLookup.computeIfAbsent(DEFAULT_SINGLE_MANAGER_KEY, ignored -> getSingleManager());
        }
    }

    @NonNull
    private EmbeddedStorageManager getSingleManager() {
        Collection<BeanDefinition<EmbeddedStorageManager>> beansOfType = beanContext.getBeanDefinitions(EmbeddedStorageManager.class);
        if (beansOfType.size() != 1) {
            throw new IllegalStateException(MULTIPLE_MANAGERS_WITH_NO_QUALIFIER_MESSAGE);
        } else {
            return beanContext.getBean(beansOfType.iterator().next());
        }
    }

    @NonNull
    private EmbeddedStorageManager getManagerForName(@NonNull String name) {
        if (beanContext.containsBean(EmbeddedStorageManager.class, Qualifiers.byName(name))) {
            return beanContext.getBean(EmbeddedStorageManager.class, Qualifiers.byName(name));
        } else {
            throw new StorageInterceptorException("No storage manager found for @" + StoreReturn.class.getSimpleName() + "(name = \"" + name + "\").");
        }
    }

}
