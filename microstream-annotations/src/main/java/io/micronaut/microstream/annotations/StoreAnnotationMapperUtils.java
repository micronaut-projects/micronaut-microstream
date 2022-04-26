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

import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.core.annotation.AnnotationValueBuilder;
import io.micronaut.core.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Utility classes to map to the {@link Store} annotation.
 * @author Sergio del Amo
 * @since 1.0.0
 */
public final class StoreAnnotationMapperUtils {

    private static final String STRATEGY = "strategy";
    private static final String NAME = "name";

    private StoreAnnotationMapperUtils() {

    }

    @NonNull
    public static AnnotationValueBuilder<Store> annotationValueBuilder(@NonNull AnnotationValue<?> annotation) {
        StoringStrategy storingStrategy = annotation.get(STRATEGY, StoringStrategy.class).orElse(StoringStrategy.LAZY);
        Optional<String> name = annotation.get(NAME, String.class);
        AnnotationValueBuilder<Store> builder = AnnotationValue.builder(Store.class)
            .member(STRATEGY, storingStrategy);
        if (name.isPresent()) {
            builder = builder.member(NAME, name.get());
        }
        return builder;
    }

    @NonNull
    public static List<AnnotationValue<?>> map(@NonNull AnnotationValue<?> annotation,
                                               @NonNull Consumer<AnnotationValueBuilder<Store>> builderConsumer) {
        AnnotationValueBuilder<Store> builder = StoreAnnotationMapperUtils.annotationValueBuilder(annotation);
        builderConsumer.accept(builder);
        List<AnnotationValue<?>> annotationValues = new ArrayList<>(1);
        annotationValues.add(builder.build());
        return annotationValues;
    }
}
