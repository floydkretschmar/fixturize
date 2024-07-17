package de.floydkretschmar.fixturize.stategies.constants.value;

import de.floydkretschmar.fixturize.stategies.constants.value.map.ClassValueProviderMap;
import de.floydkretschmar.fixturize.stategies.constants.value.map.ElementKindValueProviderMap;
import de.floydkretschmar.fixturize.stategies.constants.value.map.TypeKindValueProviderMap;
import lombok.RequiredArgsConstructor;

import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;

/**
 * Decides which value during constant creation should be used for a given {@link VariableElement}.
 *
 * @author Floyd Kretschmar
 */
@RequiredArgsConstructor
public class ConstantValueProviderService implements ValueProviderService {
    /**
     * All default and custom value providers for {@link TypeKind}s.
     */
    private final TypeKindValueProviderMap typeKindValueProviders;
    /**
     * All default and custom value providers for {@link javax.lang.model.element.ElementKind}s.
     */
    private final ElementKindValueProviderMap elementKindValueProviderMap;
    /**
     * All default and custom value providers for classes.
     */
    private final ClassValueProviderMap classValueProviders;

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
        if (typeKindValueProviders.containsKey(typeKind))
            return typeKindValueProviders.get(typeKind).provideValueAsString(field);

        final var fullQualifiedTypeName = fieldType.toString();
        final var genericStartIndex = fullQualifiedTypeName.indexOf('<');
        final var classKey = genericStartIndex > 0 ? fullQualifiedTypeName.substring(0, genericStartIndex) : fullQualifiedTypeName;
        if (classValueProviders.containsKey(classKey))
            return classValueProviders.get(classKey).provideValueAsString(field);

        if (typeKind == TypeKind.DECLARED) {
            final var declaredElement = ((DeclaredType) fieldType).asElement();
            final var elementKind = declaredElement.getKind();
            if (elementKindValueProviderMap.containsKey(elementKind))
                return elementKindValueProviderMap.get(elementKind).provideValueAsString(field);
        }

        return "null";
    }
}
