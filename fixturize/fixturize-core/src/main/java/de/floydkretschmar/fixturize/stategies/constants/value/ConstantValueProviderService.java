package de.floydkretschmar.fixturize.stategies.constants.value;

import de.floydkretschmar.fixturize.domain.Names;
import de.floydkretschmar.fixturize.stategies.constants.value.providers.RecursiveValueProvider;
import lombok.RequiredArgsConstructor;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;

import static javax.lang.model.element.ElementKind.CLASS;
import static javax.lang.model.element.ElementKind.ENUM;
import static javax.lang.model.type.TypeKind.ARRAY;

/**
 * Decides which value during constant creation should be used for a given {@link VariableElement}.
 *
 * @author Floyd Kretschmar
 */
@RequiredArgsConstructor
public class ConstantValueProviderService implements ValueProviderService {
    /**
     * The default constant value if all other strategies for generation fail.
     */
    public static final String DEFAULT_VALUE = "null";
    /**
     * All default and custom value providers for classes.
     */
    private final ValueProviderMap valueProviders;

    /**
     * The value provider that provides the fallback value if no other value provider has been registered.
     */
    private final RecursiveValueProvider fallbackValueProvider;

    /**
     * Returns the correct value that should be used for constant generation for the specified field.
     *
     * @param field - for which the value is being retrieved
     * @return the value used for constant construction
     */
    @Override
    public String getValueFor(VariableElement field) {
        final var fieldType = field.asType();
        final var typeKind = fieldType.getKind();

        final var names = Names.from(fieldType.toString());
        if (valueProviders.containsKey(names.getQualifiedClassNameWithoutGeneric()))
            return valueProviders.get(names.getQualifiedClassNameWithoutGeneric()).provideValueAsString(field, names);

        if (typeKind == TypeKind.DECLARED) {
            final var declaredElement = ((DeclaredType) fieldType).asElement();
            final var elementKind = declaredElement.getKind();
            if (elementKind == ENUM) {
                return provideValueForEnum(declaredElement, names);
            } else if (elementKind == CLASS) {
                return this.fallbackValueProvider.recursivelyProvideValue(field, names, this);
            }
        }

        if (typeKind == ARRAY) {
            return "new %s {}".formatted(field.asType().toString());
        }

        return DEFAULT_VALUE;
    }


    private String provideValueForEnum(Element declaredElement, Names names) {
        final var firstEnumElement = declaredElement.getEnclosedElements().stream()
                .filter(element -> element.getKind().equals(ElementKind.ENUM_CONSTANT))
                .map(Object::toString)
                .findFirst();

        if (firstEnumElement.isEmpty())
            return DEFAULT_VALUE;

        return "%s.%s".formatted(names.getQualifiedClassName(), firstEnumElement.get());
    }
}
