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

import io.micronaut.context.annotation.EachBean;
import io.micronaut.context.annotation.Factory;
import jakarta.inject.Singleton;
import one.microstream.persistence.binary.jdk17.types.BinaryHandlersJDK17;
import one.microstream.persistence.binary.jdk8.types.BinaryHandlersJDK8;
import one.microstream.storage.embedded.types.EmbeddedStorageFoundation;

/**
 * This Factory instantiates a {@link one.microstream.storage.embedded.types.EmbeddedStorageFoundation} for each {@link EmbeddedStorageConfigurationProvider}.
 * @author Sergio del Amo
 * @since 1.0.0
 */
@Factory
public class EmbeddedStorageFoundationFactory {

    /**
     *
     * @param provider A {@link one.microstream.storage.embedded.configuration.types.EmbeddedStorageConfiguration#Builder()} provider.
     * @return A {@link one.microstream.storage.embedded.types.EmbeddedStorageFoundation}.
     */
    @EachBean(EmbeddedStorageConfigurationProvider.class)
    @Singleton
    EmbeddedStorageFoundation<?> createFoundation(EmbeddedStorageConfigurationProvider provider) {
        return provider.getBuilder().createEmbeddedStorageFoundation()
            .onConnectionFoundation(BinaryHandlersJDK8::registerJDK8TypeHandlers)
            .onConnectionFoundation(BinaryHandlersJDK17::registerJDK17TypeHandlers);
    }
}
