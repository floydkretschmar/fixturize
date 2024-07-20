package de.floydkretschmar.fixturize.stategies.constants.value.providers.fallback;

import de.floydkretschmar.fixturize.domain.Names;
import de.floydkretschmar.fixturize.stategies.constants.value.providers.ValueProvider;
import lombok.RequiredArgsConstructor;

import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;

import static javax.lang.model.element.ElementKind.CLASS;
import static javax.lang.model.element.ElementKind.ENUM;

@RequiredArgsConstructor
public class DeclaredTypeValueProvider implements ValueProvider {
    private final ValueProvider enumValueProvider;

    private final ValueProvider classValueProvider;

    @Override
    public String provideValueAsString(VariableElement field, Names names) {
        final var fieldType = field.asType();
        final var typeKind = fieldType.getKind();

        if (typeKind == TypeKind.DECLARED) {
            final var declaredElement = ((DeclaredType) fieldType).asElement();
            final var elementKind = declaredElement.getKind();
            if (elementKind == ENUM) {
                return this.enumValueProvider.provideValueAsString(field, names);
            } else if (elementKind == CLASS) {
                return this.classValueProvider.provideValueAsString(field, names);
            }
        }

        return DEFAULT_VALUE;
    }
}
