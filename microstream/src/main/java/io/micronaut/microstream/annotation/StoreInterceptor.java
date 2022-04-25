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

import io.micronaut.aop.InterceptedMethod;
import io.micronaut.aop.InterceptorBean;
import io.micronaut.aop.MethodInterceptor;
import io.micronaut.aop.MethodInvocationContext;
import io.micronaut.context.BeanContext;
import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.type.MutableArgumentValue;
import io.micronaut.core.util.CollectionUtils;
import io.micronaut.core.util.StringUtils;
import io.micronaut.inject.BeanDefinition;
import io.micronaut.inject.qualifiers.Qualifiers;
import jakarta.inject.Singleton;
import one.microstream.concurrency.XThreads;
import one.microstream.storage.embedded.types.EmbeddedStorageManager;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Sergio del Amo
 * @since 1.0.0
 */
@Singleton
@InterceptorBean(Store.class)
public class StoreInterceptor implements MethodInterceptor<Object, Object> {

    public static final String MULTIPLE_MANAGERS_WITH_NO_QUALIFIER_MESSAGE = "Multiple storage managers found, but no name was specified.";

    private static final String DEFAULT_SINGLE_MANAGER_KEY = "__default__";
    private static final String PARAMETERS = "parameters";
    private static final String RESULT = "result";

    private final ConcurrentHashMap<String, EmbeddedStorageManager> managerLookup = new ConcurrentHashMap<>();
    private final BeanContext beanContext;

    public StoreInterceptor(BeanContext beanContext) {
        this.beanContext = beanContext;
    }

    @Nullable
    @Override
    public Object intercept(MethodInvocationContext<Object, Object> context) {
        AnnotationValue<Store> storeAnnotationValue = context.getAnnotation(Store.class);
        if (storeAnnotationValue == null) {
            return context.proceed();
        }
        @SuppressWarnings("resource") // We don't want to close the storage manager
        EmbeddedStorageManager manager = lookupManager(storeAnnotationValue);

        InterceptedMethod interceptedMethod = InterceptedMethod.of(context);
        switch (interceptedMethod.resultType()) {
            case PUBLISHER:
                return interceptedMethod.handleResult(Flux.from(interceptedMethod.interceptResultAsPublisher())
                    .doOnNext(o -> store(manager, context, storeAnnotationValue, o))
                );
            case COMPLETION_STAGE:
                return interceptedMethod.interceptResultAsCompletionStage()
                    .whenComplete((o, t) -> {
                        store(manager, context, storeAnnotationValue, o);
                    });
            case SYNCHRONOUS:
                Object result = context.proceed();
                store(manager, context, storeAnnotationValue, result);
                return result;
            default:
                return interceptedMethod.unsupported();
        }
    }

    private void store(@NonNull EmbeddedStorageManager embeddedStorageManager,
                       @NonNull MethodInvocationContext<Object, Object> context,
                       @NonNull AnnotationValue<Store> storeAnnotationValue,
                       @Nullable Object result) {
        List<Object> objects = targetParametersValues(context, storeAnnotationValue);
        if (result != null && storeAnnotationValue.booleanValue(RESULT).orElse(false)) {
            objects.add(result);
        }
        if (CollectionUtils.isNotEmpty(objects)) {
            store(embeddedStorageManager, objects);
        }
    }

    private void store(@NonNull EmbeddedStorageManager embeddedStorageManager,
                       @NonNull List<Object> instances) {
        XThreads.executeSynchronized(() -> embeddedStorageManager.storeAll(instances));
    }

    @NonNull
    private EmbeddedStorageManager lookupManager(@NonNull AnnotationValue<Store> storeAnnotationValue) {
        String name = Optional.ofNullable(storeAnnotationValue)
            .flatMap(a -> a.stringValue("name")).orElse(null);
        return lookupManager(name);
    }

    @NonNull
    private EmbeddedStorageManager lookupManager(@Nullable String name) {
        if (StringUtils.isNotEmpty(name)) {
            return managerLookup.computeIfAbsent(name, this::getManagerForName);
        } else {
            return managerLookup.computeIfAbsent(DEFAULT_SINGLE_MANAGER_KEY, ignored -> getSingleManager());
        }
    }

    @NonNull
    private EmbeddedStorageManager getSingleManager() {
        Collection<BeanDefinition<EmbeddedStorageManager>> beansOfType = beanContext.getBeanDefinitions(EmbeddedStorageManager.class);
        if (beansOfType.size() != 1) {
            throw new IllegalStateException(MULTIPLE_MANAGERS_WITH_NO_QUALIFIER_MESSAGE);
        } else {
            return beanContext.getBean(beansOfType.iterator().next());
        }
    }

    @NonNull
    private EmbeddedStorageManager getManagerForName(@NonNull String name) {
        if (beanContext.containsBean(EmbeddedStorageManager.class, Qualifiers.byName(name))) {
            return beanContext.getBean(EmbeddedStorageManager.class, Qualifiers.byName(name));
        } else {
            throw new StorageInterceptorException("No storage manager found for @" + Store.class.getSimpleName() + "(name = \"" + name + "\").");
        }
    }

    @NonNull
    private static Map<String, MutableArgumentValue<?>> targetParamters(@NonNull MethodInvocationContext<Object, Object> context,
                                                                        @NonNull AnnotationValue<Store> storeAnnotationValue) {
        Map<String, MutableArgumentValue<?>> parameters = context.getParameters();
        Map<String, MutableArgumentValue<?>> targetParameters = new HashMap<>();
        String[] storeAnnotationValueParameters = storeAnnotationValue.stringValues(PARAMETERS);
        for (Map.Entry<String, MutableArgumentValue<?>> entry : parameters.entrySet()) {
            if (Arrays.stream(storeAnnotationValueParameters).anyMatch(it -> it.equalsIgnoreCase(entry.getKey()))) {
                targetParameters.put(entry.getKey(), entry.getValue());
            }
        }
        return targetParameters;
    }

    @NonNull
    private static List<Object> targetParametersValues(@NonNull MethodInvocationContext<Object, Object> context,
                                                       @NonNull AnnotationValue<Store> storeAnnotationValue) {
        Map<String, MutableArgumentValue<?>> params = targetParamters(context, storeAnnotationValue);
        List<Object> result = new ArrayList<>();
        for (Map.Entry<String, MutableArgumentValue<?>> entry : params.entrySet()) {
            MutableArgumentValue<?> argumentValue = entry.getValue();
            Object obj = argumentValue.getValue();
            result.add(obj);
        }
        return result;
    }

}
