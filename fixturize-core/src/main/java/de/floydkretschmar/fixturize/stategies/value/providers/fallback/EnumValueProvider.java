package de.floydkretschmar.fixturize.stategies.value.providers.fallback;

import de.floydkretschmar.fixturize.stategies.metadata.TypeMetadata;
import de.floydkretschmar.fixturize.stategies.value.providers.FallbackValueProvider;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.type.DeclaredType;

import static javax.lang.model.element.ElementKind.ENUM;
import static javax.lang.model.type.TypeKind.DECLARED;

/**
 * Default value provider for enums.
 */
public class EnumValueProvider implements FallbackValueProvider {

    @Override
    public boolean canProvideFallback(Element element, TypeMetadata metadata) {
        final var type = element.asType();
        if (type.getKind() == DECLARED && type instanceof DeclaredType declaredType) {
            final var declaredElement = declaredType.asElement();
            return declaredElement.getKind() == ENUM;
        }

        return false;
    }

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
