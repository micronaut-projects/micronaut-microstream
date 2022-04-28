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
package io.micronaut.microstream.persistence;

import io.micronaut.context.event.BeanCreatedEvent;
import io.micronaut.context.event.BeanCreatedEventListener;
import io.micronaut.core.annotation.NonNull;
import jakarta.inject.Singleton;
import one.microstream.persistence.binary.jdk8.types.BinaryHandlersJDK8;
import one.microstream.storage.embedded.types.EmbeddedStorageFoundation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles the creation of {@link EmbeddedStorageFoundation} instances, and binds the JDK8 binary handlers to them.
 *
 * @author Tim Yates
 * @since 1.0.0
 */
@Singleton
public class EmbeddedStorageFoundationJDK8TypeHandlers implements BeanCreatedEventListener<EmbeddedStorageFoundation<?>> {

    private static final Logger LOG = LoggerFactory.getLogger(EmbeddedStorageFoundationJDK8TypeHandlers.class);

    @Override
    public EmbeddedStorageFoundation<?> onCreated(@NonNull BeanCreatedEvent<EmbeddedStorageFoundation<?>> event) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Registering JDK8 Foundation Persistence Type Handlers");
        }
        EmbeddedStorageFoundation<?> bean = event.getBean();
        bean.onConnectionFoundation(BinaryHandlersJDK8::registerJDK8TypeHandlers);
        return bean;
    }
}
