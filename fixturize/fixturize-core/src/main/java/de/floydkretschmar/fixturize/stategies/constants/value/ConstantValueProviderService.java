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
        if (typeKindValueProviders.containsKey(fieldType.getKind()))
            return typeKindValueProviders.get(fieldType.getKind()).provideValueAsString(field);

        if (fieldType.getKind() == TypeKind.DECLARED) {
            final var declaredElement = ((DeclaredType)fieldType).asElement();
            if (elementKindValueProviderMap.containsKey(declaredElement.getKind()))
                return elementKindValueProviderMap.get(declaredElement.getKind()).provideValueAsString(declaredElement);
        }

        if (declaredKindValueProviders.containsKey(fieldType.toString()))
            return declaredKindValueProviders.get(fieldType.toString()).provideValueAsString(field);

        return "null";
    }
}
