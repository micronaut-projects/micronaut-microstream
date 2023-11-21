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

import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.core.annotation.Internal;
import io.micronaut.inject.annotation.TypedAnnotationMapper;
import io.micronaut.inject.visitor.VisitorContext;
import java.util.List;


/**
 * Maps the {@link StoreRoot} annotation to the {@link Store} annotation.
 * @author Sergio del Amo
 * @since 1.0.0
 */
@Internal
public class StoreRootAnnotationMapper implements TypedAnnotationMapper<StoreRoot> {

    @Override
    public List<AnnotationValue<?>> map(AnnotationValue<StoreRoot> annotation, VisitorContext visitorContext) {
        return StoreAnnotationMapperUtils.map(annotation, builder -> builder.member("root", true));
    }

    @Override
    public Class<StoreRoot> annotationType() {
        return StoreRoot.class;
    }
}
