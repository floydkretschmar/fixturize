package de.floydkretschmar.fixturize.stategies.constants.value.providers.fallback;

import de.floydkretschmar.fixturize.domain.Names;
import de.floydkretschmar.fixturize.stategies.constants.value.providers.ValueProvider;
import lombok.RequiredArgsConstructor;

import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import static javax.lang.model.type.TypeKind.ARRAY;

@RequiredArgsConstructor
public class ContainerValueProvider implements ValueProvider {
    /**
     * The utility object used to process {@link javax.lang.model.element.Element} instances
     */
    private final Elements elementUtils;

    /**
     * The utility object used to process {@link javax.lang.model.type.TypeMirror} instances
     */
    private final Types typeUtils;

    /**
     * The value provider that provides the fallback value for containers (arrays, collections) if no other value provider
     * has been registered.
     */
    private final ValueProvider arrayValueProvider;

    private static final Map<Class<?>, String> SUPPORTED_CONTAINER_CLASSES = Map.of(
            List.class, "java.util.List.of()",
            Map.class, "java.util.Map.of()",
            Set.class, "java.util.Set.of()",
            Queue.class, "new java.util.PriorityQueue<>()",
            Collection.class, "java.util.List.of()");

    @Override
    public String provideValueAsString(VariableElement field, Names names) {
        final var fieldType = field.asType();
        final var typeKind = fieldType.getKind();

        if (typeKind == ARRAY) {
            return this.arrayValueProvider.provideValueAsString(field, names);
        }

        for (var entry : SUPPORTED_CONTAINER_CLASSES.entrySet()) {
            if (isEqualTo(fieldType, entry.getKey())) {
                return entry.getValue();
            }
        }

        return DEFAULT_VALUE;
    }

    public boolean isEqualTo(TypeMirror fieldType, Class<?> typeClass) {
        final var erasedFieldType = typeUtils.erasure(fieldType);
        final var classType = typeUtils.erasure(elementUtils.getTypeElement(typeClass.getName()).asType());
        return typeUtils.isSameType(erasedFieldType, classType);
    }
}
