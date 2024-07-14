package de.floydkretschmar.fixturize.stategies.constants.value;

import lombok.RequiredArgsConstructor;

import javax.lang.model.element.VariableElement;

@RequiredArgsConstructor
public class ConstantValueProviderService implements ValueProviderService {
    private final TypeKindValueProviderMap typeKindValueProviders;
    private final DeclaredKindValueProviderMap declaredKindValueProviders;
    @Override
    public String getValueFor(VariableElement field) {
        final var fieldType = field.asType();
        if (typeKindValueProviders.containsKey(fieldType.getKind()))
            return typeKindValueProviders.get(fieldType.getKind()).provideValueAsString(field);
        else if (declaredKindValueProviders.containsKey(fieldType.toString()))
            return declaredKindValueProviders.get(fieldType.toString()).provideValueAsString(field);

        return "null";
    }
}
