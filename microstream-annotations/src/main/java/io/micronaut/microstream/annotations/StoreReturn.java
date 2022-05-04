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
package io.micronaut.microstream.annotations;

import io.micronaut.context.annotation.AliasFor;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 *
 * An around annotation for methods which simplifies storing objects in an associated Store Manager.
 *
 * <p>
 * This annotation will wrap the decorated method to ensure thread isolation.
 * </p>
 * <p>
 * You can store a Method return statement.
 * </p>
 *
 * <p>
 * A method such as this:
 * </p>
 * <pre>
 * {@literal @StoreReturn}
 * protected Customer updateCustomer(String id, CustomerSave customerSave) {
 *     Customer c = data().getCustomers().get(id);
 *     if (c != null) {
 *         c.setFirstName(customerSave.getFirstName());
 *         c.setLastName(customerSave.getLastName());
 *         return c;
 *     }
 *     return null;
 * }
 * </pre>
 * <p>
 * Becomes
 * </p>
 * <pre>
 * protected Customer updateCustomer(String id, CustomerSave customerSave) {
 *     XThreads.executeSynchronized(() -&gt; {
 *         Customer c = data().getCustomers().get(id);
 *         if (c != null) {
 *             c.setFirstName(customerSave.getFirstName());
 *             c.setLastName(customerSave.getLastName());
 *             embeddedStorageManager.store(c);
 *             return c;
 *         }
 *         return null;
 *     }
 * }
 * </pre>
 *
 * @see <a href="https://docs.microstream.one/manual/storage/root-instances.html#_shared_mutable_data">Microstream mutable data docs.</a>
 * @since 1.0.0
 * @author Sergio del Amo
 */
@Documented
@Retention(RUNTIME)
@Target({ElementType.METHOD})
public @interface StoreReturn {
    /**
     * The optional name qualifier of the Storage Manager to use.
     * If your application only have a Microstream instance, this is not required
     *
     * @return The name qualifier of the Storage Manager to use.
     */
    @AliasFor(member = "value")
    String name() default "";

    /**
     * The Storing strategy. Defaults to Lazy.
     * @return Storing Strategy;
     */
    StoringStrategy strategy() default StoringStrategy.LAZY;
}
