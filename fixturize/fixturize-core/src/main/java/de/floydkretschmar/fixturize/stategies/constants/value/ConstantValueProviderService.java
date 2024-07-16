package de.floydkretschmar.fixturize.stategies.constants.value;

import de.floydkretschmar.fixturize.stategies.constants.value.map.ClassValueProviderMap;
import de.floydkretschmar.fixturize.stategies.constants.value.map.ElementKindValueProviderMap;
import de.floydkretschmar.fixturize.stategies.constants.value.map.TypeKindValueProviderMap;
import lombok.RequiredArgsConstructor;

import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;

@RequiredArgsConstructor
public class ConstantValueProviderService implements ValueProviderService {
    private final TypeKindValueProviderMap typeKindValueProviders;
    private final ElementKindValueProviderMap elementKindValueProviderMap;
    private final ClassValueProviderMap declaredKindValueProviders;
    @Override
    public String getValueFor(VariableElement field) {
        final var fieldType = field.asType();
        final var typeKind = fieldType.getKind();
        if (typeKindValueProviders.containsKey(typeKind))
            return typeKindValueProviders.get(typeKind).provideValueAsString(field);

        if (typeKind == TypeKind.DECLARED) {
            final var declaredElement = ((DeclaredType)fieldType).asElement();
            final var elementKind = declaredElement.getKind();
            if (elementKindValueProviderMap.containsKey(elementKind))
                return elementKindValueProviderMap.get(elementKind).provideValueAsString(field);
        }

        final var fullQualifiedTypeName = fieldType.toString();
        final var genericStartIndex = fullQualifiedTypeName.indexOf('<');
        final var classKey = genericStartIndex > 0 ? fullQualifiedTypeName.substring(0, genericStartIndex) : fullQualifiedTypeName;
        if (declaredKindValueProviders.containsKey(classKey))
            return declaredKindValueProviders.get(classKey).provideValueAsString(field);

        return "null";
    }
}
