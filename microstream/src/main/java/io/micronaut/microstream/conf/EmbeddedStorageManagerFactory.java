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
package io.micronaut.microstream.conf;

import io.micronaut.context.BeanContext;
import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.EachBean;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Parameter;
import io.micronaut.context.exceptions.DisabledBeanException;
import io.micronaut.inject.qualifiers.Qualifiers;
import jakarta.inject.Singleton;
import one.microstream.persistence.binary.jdk8.types.BinaryHandlersJDK8;
import one.microstream.storage.embedded.types.EmbeddedStorageFoundation;
import one.microstream.storage.embedded.types.EmbeddedStorageManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Sergio del Amo
 * @since 1.0.0
 */
@Factory
public class EmbeddedStorageManagerFactory {
    private static final Logger LOG = LoggerFactory.getLogger(EmbeddedStorageManagerFactory.class);

    private final BeanContext beanContext;

    /**
     * Constructor.
     * @param beanContext Bean Context.
     */
    public EmbeddedStorageManagerFactory(BeanContext beanContext) {
        this.beanContext = beanContext;
    }

    /**
     *
     * @param foundation EmbeddedStorageFoundation
     * @param name Name qualifier
     * @return EmbeddedStorageManager
     */
    @EachBean(EmbeddedStorageFoundation.class)
    @Bean(preDestroy = "shutdown")
    @Singleton
    public EmbeddedStorageManager createEmbeddedStorageManager(EmbeddedStorageFoundation<?> foundation,
                                                               @Parameter String name) {
        foundation.onConnectionFoundation(BinaryHandlersJDK8::registerJDK8TypeHandlers);
        @SuppressWarnings("java:S2095") // We don't want to close the storage manager
        EmbeddedStorageManager storageManager = foundation.createEmbeddedStorageManager().start();
        if (storageManager.root() == null) {
            if (LOG.isTraceEnabled()) {
                LOG.trace("No data found");
            }
            if (!beanContext.containsBean(RootInstanceProvider.class, Qualifiers.byName(name))) {
                throw new DisabledBeanException("Please, define a bean of type " + RootInstanceProvider.class.getSimpleName() + " by name qualifier: " + name);
            }
            RootInstanceProvider<?> rootInstanceProvider = beanContext.getBean(RootInstanceProvider.class, Qualifiers.byName(name));
            storageManager.setRoot(rootInstanceProvider.rootInstance());
            storageManager.storeRoot();
        }
        return storageManager;
    }
}
