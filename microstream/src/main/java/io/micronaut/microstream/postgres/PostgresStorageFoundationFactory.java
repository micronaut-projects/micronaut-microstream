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
package io.micronaut.microstream.postgres;

import io.micronaut.context.BeanContext;
import io.micronaut.context.annotation.EachBean;
import io.micronaut.context.annotation.Factory;
import io.micronaut.inject.qualifiers.Qualifiers;
import jakarta.inject.Singleton;
import one.microstream.afs.sql.types.SqlConnector;
import one.microstream.afs.sql.types.SqlFileSystem;
import one.microstream.afs.sql.types.SqlProviderPostgres;
import one.microstream.storage.embedded.types.EmbeddedStorage;
import one.microstream.storage.embedded.types.EmbeddedStorageFoundation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;

/**
 * Factory for an S3 based EmbeddedStorageFoundation.
 *
 * @since 3.0.0
 * @author Tim Yates
 */
@Factory
public class PostgresStorageFoundationFactory {

    private static final Logger LOG = LoggerFactory.getLogger(PostgresStorageFoundationFactory.class);

    /**
     * @param ctx Bean Context.
     * @param provider A {@link one.microstream.storage.embedded.configuration.types.EmbeddedStorageConfiguration#Builder()} provider.
     * @return A {@link EmbeddedStorageFoundation}.
     */
    @Singleton
    @EachBean(PostgresStorageConfigurationProvider.class)
    EmbeddedStorageFoundation<?> createFoundation(PostgresStorageConfigurationProvider provider, BeanContext ctx) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Creating storage foundation from postgres storage provider {}", provider);
        }
        DataSource dataSource = ctx.findBean(DataSource.class, Qualifiers.byName(provider.getName()))
            .orElseGet(() -> defaultDataSource(ctx));

        if (LOG.isDebugEnabled()) {
            LOG.debug("Got DataSource {}", dataSource);
        }
        SqlFileSystem fileSystem = SqlFileSystem.New(
            SqlConnector.Caching(
                SqlProviderPostgres.New(dataSource)
            )
        );

        return EmbeddedStorage.Foundation((fileSystem.ensureDirectoryPath(provider.getTableName())));
    }

    private DataSource defaultDataSource(BeanContext ctx) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("No named DataSource found. Looking for a default");
        }
        DataSource bean = ctx.getBean(DataSource.class);
        if (LOG.isTraceEnabled()) {
            LOG.trace("Found a datasource {}", bean);
        }
        return bean;
    }
}
