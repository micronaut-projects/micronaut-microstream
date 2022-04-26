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
import io.micronaut.core.annotation.Internal;
import io.micronaut.inject.annotation.TypedAnnotationMapper;
import io.micronaut.inject.visitor.VisitorContext;

import java.util.List;


/**
 * Maps the {@link StoreParams} annotation to the {@link Store} annotation.
 * @author Sergio del Amo
 * @since 1.0.0
 */
@Internal
public class StoreParamsAnnotationMapper implements TypedAnnotationMapper<StoreParams> {

    public static final String PARAMETERS = "parameters";

    @Override
    public List<AnnotationValue<?>> map(AnnotationValue<StoreParams> annotation, VisitorContext visitorContext) {
        return StoreAnnotationMapperUtils.map(annotation, builder -> builder.member(PARAMETERS, annotation.stringValues()));
    }

    @Override
    public Class<StoreParams> annotationType() {
        return StoreParams.class;
    }
}
