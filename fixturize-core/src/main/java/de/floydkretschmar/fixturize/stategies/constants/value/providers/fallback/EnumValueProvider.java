package de.floydkretschmar.fixturize.stategies.constants.value.providers.fallback;

import de.floydkretschmar.fixturize.domain.TypeMetadata;
import de.floydkretschmar.fixturize.stategies.constants.value.providers.ValueProvider;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.type.DeclaredType;

/**
 * Default value provider for enums.
 */
public class EnumValueProvider implements ValueProvider {
    @Override
    public String provideValueAsString(Element field, TypeMetadata metadata) {
        final var fieldType = field.asType();
        final var declaredElement = ((DeclaredType) fieldType).asElement();
        final var firstEnumElement = declaredElement.getEnclosedElements().stream()
                .filter(element -> element.getKind().equals(ElementKind.ENUM_CONSTANT))
                .map(Object::toString)
                .findFirst();

        if (firstEnumElement.isEmpty())
            return DEFAULT_VALUE;

        return "%s.%s".formatted(metadata.getQualifiedClassName(), firstEnumElement.get());
    }
}
