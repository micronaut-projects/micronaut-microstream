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

import io.micronaut.aop.Around;
import io.micronaut.context.annotation.AliasFor;
import io.micronaut.context.annotation.Type;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * This annotation will wrap the decorated method to ensure thread isolation, and call storeAll on
 * the updated graph. The return value of the wrapped method will be retained.
 * <p>
 * A method such as this:
 * <pre>
 * {@literal @Store}
 *  public String set(){
 *      root.changeData();
 *  }
 * </pre>
 * <p>
 * Will be decorated to become:
 * <pre>
 * {@literal @Store}
 *  public String set() {
 *      XThreads.executeSynchronized(() -> {
 *          root.changeData();
 *          manager.storeAll();
 *      });
 *  }
 * </pre>
 *
 * @see <a href="https://docs.microstream.one/manual/storage/root-instances.html#_shared_mutable_data">Microstream mutable data docs.</a>
 * @since 1.0.0
 * @author Tim Yates
 */
@Documented
@Retention(RUNTIME)
@Target({ElementType.METHOD})
@Around
public @interface Store {

    /**
     * The optional name qualifier of the store to use.
     * If your application only have a Microstream instance, this is not required
     *
     * @return The name of the store
     */
    @AliasFor(member = "name")
    String value() default "";

    /**
     * The optional name qualifier of the store to use.
     * If your application only have a Microstream instance, this is not required
     *
     * @return The name of the store
     */
    @AliasFor(member = "value")
    String name() default "";

    /**
     *
     * @return parameters name which should be stored in the associated embeddded storage manager.
     */
    String[] parameters() default {};

    /**
     *
     * @return Whether to store result.
     */
    boolean result() default false;
}
