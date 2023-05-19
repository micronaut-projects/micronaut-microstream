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
/**
 * MicroStream Storage Target support for Postgres.
 *
 * @since 2.0.0
 * @author Tim Yates
 */
@Requires(classes = {SqlProviderPostgres.class, SqlFileSystem.class})
@Requires(beans = DataSource.class)
package io.micronaut.microstream.postgres;

import io.micronaut.context.annotation.Requires;
import one.microstream.afs.sql.types.SqlFileSystem;
import one.microstream.afs.sql.types.SqlProviderPostgres;

import javax.sql.DataSource;
