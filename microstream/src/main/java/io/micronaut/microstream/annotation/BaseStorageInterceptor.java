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

import io.micronaut.aop.MethodInterceptor;
import io.micronaut.context.BeanContext;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.util.StringUtils;
import io.micronaut.inject.BeanDefinition;
import io.micronaut.inject.qualifiers.Qualifiers;
import one.microstream.storage.embedded.types.EmbeddedStorageManager;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

abstract class BaseStorageInterceptor implements MethodInterceptor<Object, Object>  {

    public static final String MULTIPLE_MANAGERS_WITH_NO_QUALIFIER_MESSAGE = "Multiple storage managers found, but no name was specified.";

    private static final String DEFAULT_SINGLE_MANAGER_KEY = "__default__";

    private final ConcurrentHashMap<String, EmbeddedStorageManager> managerLookup = new ConcurrentHashMap<>();
    private final BeanContext beanContext;

    protected BaseStorageInterceptor(BeanContext beanContext) {
        this.beanContext = beanContext;
    }

    @NonNull
    protected EmbeddedStorageManager lookupManager(@Nullable String name) {
        if (StringUtils.isNotEmpty(name)) {
            return managerLookup.computeIfAbsent(name, this::getManagerForName);
        } else {
            return managerLookup.computeIfAbsent(DEFAULT_SINGLE_MANAGER_KEY, ignored -> getSingleManager());
        }
    }

    @NonNull
    protected EmbeddedStorageManager getSingleManager() {
        Collection<BeanDefinition<EmbeddedStorageManager>> beansOfType = beanContext.getBeanDefinitions(EmbeddedStorageManager.class);
        if (beansOfType.size() != 1) {
            throw new IllegalStateException(MULTIPLE_MANAGERS_WITH_NO_QUALIFIER_MESSAGE);
        } else {
            return beanContext.getBean(beansOfType.iterator().next());
        }
    }

    @NonNull
    protected EmbeddedStorageManager getManagerForName(@NonNull String name) {
        if (beanContext.containsBean(EmbeddedStorageManager.class, Qualifiers.byName(name))) {
            return beanContext.getBean(EmbeddedStorageManager.class, Qualifiers.byName(name));
        } else {
            throw new StorageInterceptorException("No storage manager found for @" + StoreAll.class.getSimpleName() + "(name = \"" + name + "\").");
        }
    }

}
