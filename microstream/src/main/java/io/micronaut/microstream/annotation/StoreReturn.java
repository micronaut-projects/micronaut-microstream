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
import io.micronaut.context.annotation.Type;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * This annotation will wrap the decorated method to ensure thread isolation, and call store on
 * the returned value. The return value of the wrapped method will be retained.
 * <p>
 * A method such as this:
 * <pre>
 * {@literal @StoreReturn}
 *  public User set() {
 *      User user = root.getUser();
 *      user.setFirstName("Tim");
 *      return user;
 *  }
 * </pre>
 * <p>
 * Will be decorated to effectively become:
 * <pre>
 * {@literal @StoreReturn}
 *  public String set() {
 *      User user = root.getUser();
 *      XThreads.executeSynchronized(() -> {
 *          user.setFirstName("Tim");
 *          manager.store(user);
 *      });
 *      return user;
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
@Type(StoreReturnInterceptor.class)
@Around
public @interface StoreReturn {

    /**
     * The optional name qualifier of the store to use. If there is only a single store, this is not required.
     *
     * @return The name of the store
     */
    String name() default "";
}
