package de.floydkretschmar.fixturize.stategies.constants.value.providers.fallback;

import de.floydkretschmar.fixturize.domain.Names;
import de.floydkretschmar.fixturize.stategies.constants.value.providers.ValueProvider;

import javax.lang.model.element.VariableElement;

import static javax.lang.model.type.TypeKind.ARRAY;

public class ContainerValueProvider implements ValueProvider {
    @Override
    public String provideValueAsString(VariableElement field, Names names) {
        final var fieldType = field.asType();
        final var typeKind = fieldType.getKind();
        if (typeKind == ARRAY) {
            return "new %s {}".formatted(names.getQualifiedClassName());
        }

        return DEFAULT_VALUE;
    }
}
