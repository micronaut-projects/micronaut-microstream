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
 * You can store method parameters.
 * </p>
 * <p>
 * A method such as this:
 * <pre>
 * {@literal @Store(parameters = "customers")}
 * protected Customer addCustomer(Map&lt;String, Customer&gt; customers, CustomerSave customerSave) {
 *     String id = UUID.randomUUID().toString();
 *     Customer customer = new Customer(id, customerSave.getFirstName(), customerSave.getLastName());
 *     customers.put(id, customer);
 *     return customer;
 * }
 * </pre>
 * <p>
 *
 * Becomes
 * <pre>
 * protected Customer addCustomer(Map&lt;String, Customer&gt; customers, CustomerSave customerSave) {
 *     return XThreads.executeSynchronized(() -&gt; {
 *         String id = UUID.randomUUID().toString();
 *         Customer customer = new Customer(id, customerSave.getFirstName(), customerSave.getLastName());
 *         customers.put(id, customer);
 *         embeddedStorageManager.store(customers);
 *         return customer;
 *     });
 * }
 * </pre>
 *
 * @see <a href="https://docs.microstream.one/manual/storage/root-instances.html#_shared_mutable_data">MicroStream mutable data docs.</a>
 * @since 1.0.0
 * @author Tim Yates
 * @author Sergio del Amo
 */
@Documented
@Retention(RUNTIME)
@Target({ElementType.METHOD})
public @interface StoreParams {
    /**
     * The optional name qualifier of the Store Manager to use.
     * If your application only have a MicroStream instance, this is not required
     *
     * @return The name qualifier of the Store Manager to use.
     */
    @AliasFor(member = "value")
    String name() default "";

    /**
     * parameters which should be stored in the associated Store Manager.
     * @return parameters name which should be stored in the associated Store Manager.
     */
    String[] value();

    /**
     * The Storing strategy. Defaults to Lazy.
     * @return Storing Strategy;
     */
    StoringStrategy strategy() default StoringStrategy.LAZY;
}
