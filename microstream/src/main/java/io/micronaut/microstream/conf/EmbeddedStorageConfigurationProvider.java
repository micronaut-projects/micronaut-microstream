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

import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.naming.Named;
import one.microstream.storage.embedded.configuration.types.EmbeddedStorageConfigurationBuilder;

/**
 * @author Sergio del Amo
 * @since 1.0.0
 */
public interface EmbeddedStorageConfigurationProvider extends Named {

    boolean DEFAULT_ENABLE_JDK17_TYPES = true;

    @NonNull
    EmbeddedStorageConfigurationBuilder getBuilder();

    /**
     * Returns the class of the Root Instance.
     * <a href="https://docs.microstream.one/manual/storage/root-instances.html">Root Instances</a>
     * @return Class for the Root Instance.
     */
    @Nullable
    Class<?> getRootClass();

    /**
     * Configure whether JDK 17 type enhancements are enabled. Defaults to {@value EmbeddedStorageConfigurationProvider#DEFAULT_ENABLE_JDK17_TYPES}.
     *
     * @since 2.0.0
     * @return whether JDK 17 type enhancements are enabled.
     */
    boolean isEnableJdk17Types();
}
