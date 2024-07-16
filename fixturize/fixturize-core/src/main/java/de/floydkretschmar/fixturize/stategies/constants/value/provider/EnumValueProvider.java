package de.floydkretschmar.fixturize.stategies.constants.value.provider;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;

public class EnumValueProvider implements ValueProvider<VariableElement> {
    @Override
    public String provideValueAsString(VariableElement field) {
        final var fieldType = field.asType();
        final var declaredElement = ((DeclaredType) fieldType).asElement();

        final var firstEnumElement = declaredElement.getEnclosedElements().stream()
                .filter(element -> element.getKind().equals(ElementKind.ENUM_CONSTANT))
                .map(Object::toString)
                .findFirst();

        if (firstEnumElement.isEmpty())
            return "null";

        return "%s.%s".formatted(declaredElement.asType().toString(), firstEnumElement.get());
    }
}
