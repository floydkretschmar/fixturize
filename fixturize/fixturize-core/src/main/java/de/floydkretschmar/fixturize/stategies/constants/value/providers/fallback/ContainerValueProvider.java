package de.floydkretschmar.fixturize.stategies.constants.value.providers.fallback;

import de.floydkretschmar.fixturize.domain.Metadata;
import de.floydkretschmar.fixturize.stategies.constants.value.ValueProviderService;
import de.floydkretschmar.fixturize.stategies.constants.value.providers.ValueProvider;
import lombok.RequiredArgsConstructor;

import javax.lang.model.element.Element;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

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

    private final ValueProviderService valueProviderService;

    private static final Map<Class<?>, String> SUPPORTED_CONTAINER_CLASSES = Map.of(
            Map.class, "java.util.Map.of(%s)",
            List.class, "java.util.List.of(%s)",
            Set.class, "java.util.Set.of(%s)",
            Queue.class, "new java.util.PriorityQueue<>(java.util.List.of(%s))",
            Collection.class, "java.util.List.of(%s)");

    @Override
    public String provideValueAsString(Element field, Metadata metadata) {
        final var fieldType = field.asType();
        final var typeKind = fieldType.getKind();

        if (typeKind == ARRAY) {
            return this.arrayValueProvider.provideValueAsString(field, metadata);
        }

        for (var entry : SUPPORTED_CONTAINER_CLASSES.entrySet()) {
            if (isEqualTo(fieldType, entry.getKey())) {
                final var declaredType = ((DeclaredType) fieldType);
                final var valuesForGenerics = declaredType.getTypeArguments().stream()
                        .map(typeUtils::asElement)
                        .map(valueProviderService::getValueFor)
                        .collect(Collectors.joining(", "));
                return entry.getValue().formatted(valuesForGenerics);
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
