package de.floydkretschmar.fixturize.stategies.constants.value.provider;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;

/**
 * Default value provider for enums.
 *
 * @author Floyd Kretschmar
 */
public class EnumValueProvider implements ValueProvider {

    /**
     * Returns the default value to use for constant construction of enum fields. In case enum constants are defined,
     * per default the first defined constant will be returned. In case no constants are defined, "null" is returned.
     *
     * @param field - for which the value should be provided
     * @return the text representation of the constant value
     */
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
