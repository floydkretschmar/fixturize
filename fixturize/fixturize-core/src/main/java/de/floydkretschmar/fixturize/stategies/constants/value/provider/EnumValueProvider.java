package de.floydkretschmar.fixturize.stategies.constants.value.provider;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;

public class EnumValueProvider implements ValueProvider<Element> {
    @Override
    public String provideValueAsString(Element declaredElement) {
        final var firstEnumElement = declaredElement.getEnclosedElements().stream()
                .filter(element -> element.getKind().equals(ElementKind.ENUM_CONSTANT))
                .map(Object::toString)
                .findFirst();

        if (firstEnumElement.isEmpty())
            return "null";

        return "%s.%s".formatted(declaredElement.asType().toString(), firstEnumElement.get());
    }
}
