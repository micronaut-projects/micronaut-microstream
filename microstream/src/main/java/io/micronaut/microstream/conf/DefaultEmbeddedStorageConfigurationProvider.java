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

import io.micronaut.context.annotation.ConfigurationBuilder;
import io.micronaut.context.annotation.EachProperty;
import io.micronaut.context.annotation.Parameter;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import one.microstream.storage.embedded.configuration.types.EmbeddedStorageConfiguration;
import one.microstream.storage.embedded.configuration.types.EmbeddedStorageConfigurationBuilder;

/**
 * @author Sergio del Amo
 * @since 1.0.0
 */
@EachProperty("microstream.storage")
public class DefaultEmbeddedStorageConfigurationProvider implements EmbeddedStorageConfigurationProvider {

    @ConfigurationBuilder
    EmbeddedStorageConfigurationBuilder builder = EmbeddedStorageConfiguration.Builder();

    @Nullable
    private Class<?> rootClass;

    private boolean enableJdk17Types = DEFAULT_ENABLE_JDK17_TYPES;

    private final String name;

    public DefaultEmbeddedStorageConfigurationProvider(@Parameter String name) {
        this.name = name;
    }

    @Override
    @NonNull
    public EmbeddedStorageConfigurationBuilder getBuilder() {
        return builder;
    }

    @Override
    @NonNull
    public String getName() {
        return name;
    }

    @Override
    @NonNull
    public Class<?> getRootClass() {
        return this.rootClass;
    }

    /**
     * Class of the Root Instance.
     * <a href="https://docs.microstream.one/manual/storage/root-instances.html">Root Instances</a>
     * @param rootClass Class for the Root Instance.
     */
    public void setRootClass(@NonNull Class<?> rootClass) {
        this.rootClass = rootClass;
    }

    @Override
    public boolean isEnableJdk17Types() {
        return enableJdk17Types;
    }

    /**
     * Configure whether JDK 17 type enhancements are enabled. Defaults to {@value EmbeddedStorageConfigurationProvider#DEFAULT_ENABLE_JDK17_TYPES}.
     *
     * @since 2.0.0
     * @param enableJdk17Types whether JDK 17 type enhancements are enabled.
     */
    public void setEnableJdk17Types(boolean enableJdk17Types) {
        this.enableJdk17Types = enableJdk17Types;
    }
}
