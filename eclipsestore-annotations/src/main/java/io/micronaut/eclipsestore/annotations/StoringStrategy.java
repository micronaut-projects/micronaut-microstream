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
package io.micronaut.eclipsestore.annotations;

/**
 * Defines the way the instance that will be stored in the Store Manager.
 * <a href="https://docs.eclipsestore.io/manual/storage/storing-data/lazy-eager-full.html">Lazy and Eager Storing</a>
 * @author Sergio del Amo
 * @since 1.0.0
 */
public enum StoringStrategy {
    /**
     * Lazy storing is the default storing mode of the EclipseStore engine.
     * Referenced instances are stored only if they have not been stored yet.
     * If a referenced instance has been stored previously it is not stored again even if it has been modified.
     * That’s why modified objects must be stored explicitly.
     */
    LAZY,
    /**
     * In eager storing mode referenced instances are stored even if they had been stored before.
     * Contrary to Lazy storing this will also store modified child objects at the cost of performance.
     */
    EAGER
}
