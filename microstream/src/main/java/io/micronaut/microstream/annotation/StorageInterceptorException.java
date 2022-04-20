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
package io.micronaut.microstream.annotation;

/**
 * Exception thrown when an error occurs during storage interceptor processing.
 *
 * @author Tim Yates
 * @since 1.0.0
 */
public class StorageInterceptorException extends RuntimeException {

    /**
     * This is thrown if there is an issue calling storeAll after the intercepted method has been executed.
     *
     * @param message The message
     * @param cause The underlying cause of the exception.
     */
    public StorageInterceptorException(String message, Throwable cause) {
        super(message, cause);
    }
}
