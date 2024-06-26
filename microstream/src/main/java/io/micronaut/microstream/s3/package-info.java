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
 * MicroStream Storage Target support for S3.
 *
 * @since 2.0.0
 * @author Tim Yates
 */
@Requires(classes = S3Connector.class)
@Requires(beans = S3Client.class)
package io.micronaut.microstream.s3;

import io.micronaut.context.annotation.Requires;
import one.microstream.afs.aws.s3.types.S3Connector;
import software.amazon.awssdk.services.s3.S3Client;

