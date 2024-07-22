package de.floydkretschmar.fixturize.stategies.constants.value.providers.fallback;

import de.floydkretschmar.fixturize.domain.Metadata;
import de.floydkretschmar.fixturize.stategies.constants.value.providers.ValueProvider;
import lombok.RequiredArgsConstructor;

import javax.lang.model.element.Element;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;

import static javax.lang.model.element.ElementKind.CLASS;
import static javax.lang.model.element.ElementKind.ENUM;

@RequiredArgsConstructor
public class DeclaredTypeValueProvider implements ValueProvider {
    private final ValueProvider enumValueProvider;

    private final ValueProvider classValueProvider;

    @Override
    public String provideValueAsString(Element field, Metadata metadata) {
        final var fieldType = field.asType();
        final var typeKind = fieldType.getKind();

        if (typeKind == TypeKind.DECLARED) {
            final var declaredElement = ((DeclaredType) fieldType).asElement();
            final var elementKind = declaredElement.getKind();
            if (elementKind == ENUM) {
                return this.enumValueProvider.provideValueAsString(field, metadata);
            } else if (elementKind == CLASS) {
                return this.classValueProvider.provideValueAsString(field, metadata);
            }
        }

        return DEFAULT_VALUE;
    }
}
