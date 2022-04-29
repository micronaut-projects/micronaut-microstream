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
package io.micronaut.microstream.rest;

import io.micronaut.context.BeanContext;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.inject.qualifiers.Qualifiers;
import io.micronaut.microstream.conf.EmbeddedStorageConfigurationProvider;
import jakarta.inject.Singleton;
import one.microstream.storage.restadapter.types.StorageRestAdapter;
import one.microstream.storage.types.StorageManager;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Default implementation of {@link MicrostreamRestService}.
 *
 * @author Tim Yates
 * @since 1.0.0
 */
@Singleton
public class DefaultMicrostreamRestService implements MicrostreamRestService {

    private final Map<String, StorageRestAdapter> adapterMap = new ConcurrentHashMap<>();
    private final String singleStorageName;

    /**
     * Creates a service that allows us to query storage managers by name.
     *
     * @param beanContext the bean context to resolve storage managers
     * @param storageFoundations the bound storage foundations
     */
    public DefaultMicrostreamRestService(
        BeanContext beanContext,
        Collection<EmbeddedStorageConfigurationProvider> storageFoundations
    ) {
        for (EmbeddedStorageConfigurationProvider storageFoundation : storageFoundations) {
            StorageManager bean = beanContext.getBean(StorageManager.class, Qualifiers.byName(storageFoundation.getName()));
            adapterMap.put(storageFoundation.getName(), StorageRestAdapter.New(bean));
        }
        this.singleStorageName = adapterMap.size() == 1 ? adapterMap.keySet().iterator().next() : null;
    }

    @NonNull
    @Override
    public StorageRestAdapter getAdapter(@NonNull String name) {
        return adapterMap.get(name);
    }

    @NonNull
    @Override
    public Optional<StorageRestAdapter> singleAdapter() {
        return singleStorageName == null ? Optional.empty() : Optional.of(adapterMap.get(singleStorageName));
    }
}
