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
 * Classes related to the use of DynamoDB as a Storage Target.
 * @author Sergio del Amo
 * @since 2.0.0
 */
@Requires(classes = {DynamoDbConnector.class, BlobStoreFileSystem.class})
@Requires(beans = {DynamoDbClient.class})
package io.micronaut.microstream.dynamodb;

import io.micronaut.context.annotation.Requires;
import one.microstream.afs.aws.dynamodb.types.DynamoDbConnector;
import one.microstream.afs.blobstore.types.BlobStoreFileSystem;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
