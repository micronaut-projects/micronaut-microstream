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

import io.micronaut.context.annotation.Requires;
import io.micronaut.context.event.BeanCreatedEvent;
import io.micronaut.context.event.BeanCreatedEventListener;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.util.StringUtils;
import jakarta.inject.Singleton;
import one.microstream.storage.types.StorageManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Tim Yates
 */
@Singleton
@Requires(bean = StorageManager.class)
@Requires(property = "microstream.rest.enabled", value = StringUtils.TRUE, defaultValue = StringUtils.FALSE)
public class StorageManagerListener implements BeanCreatedEventListener<StorageManager> {

    private static final Logger LOG = LoggerFactory.getLogger(StorageManagerListener.class);

    @Override
    public StorageManager onCreated(@NonNull BeanCreatedEvent<StorageManager> event) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("StorageManager qualified as {} created: {}", event.getBeanDefinition().getDeclaredQualifier(), event.getBean());
        }
        return event.getBean();
    }
}
