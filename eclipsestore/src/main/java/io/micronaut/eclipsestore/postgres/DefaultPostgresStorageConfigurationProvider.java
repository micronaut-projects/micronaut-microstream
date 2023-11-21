/*
 * Copyright 2017-2023 original authors
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
package io.micronaut.eclipsestore.postgres;

import io.micronaut.context.annotation.EachProperty;
import io.micronaut.context.annotation.Parameter;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;

import java.util.Optional;

/**
 * @author Tim Yates
 * @since 2.0.0
 */
@EachProperty("eclipsestore.postgres.storage")
public class DefaultPostgresStorageConfigurationProvider implements PostgresStorageConfigurationProvider {

    @NonNull
    private Class<?> rootClass;

    private final String name;

    @Nullable
    private String datasourceName;

    @NonNull
    private String tableName;

    public DefaultPostgresStorageConfigurationProvider(@Parameter String name) {
        this.name = name;
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
     * <a href="https://docs.eclipsestore.io/manual/storage/root-instances.html">Root Instances</a>
     * @param rootClass Class for the Root Instance.
     */
    public void setRootClass(@NonNull Class<?> rootClass) {
        this.rootClass = rootClass;
    }

    @Override
    @Nullable
    public Optional<String> getDatasourceName() {
        return Optional.ofNullable(datasourceName);
    }

    /**
     * The name qualifier of the defined postgres DataSource to use.
     * If unset, a datasource with the same name as the storage will be used.
     * If there is no bean with a name qualifier matching the storage name, the default datasource will be used.
     *
     * @param datasourceName
     */
    public void setDatasourceName(@Nullable String datasourceName) {
        this.datasourceName = datasourceName;
    }

    @Override
    @NonNull
    public String getTableName() {
        return tableName;
    }

    /**
     * @param tableName Name of the table to use.
     */
    public void setTableName(@NonNull String tableName) {
        this.tableName = tableName;
    }
}
