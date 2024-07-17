package de.floydkretschmar.fixturize.stategies.constants.value.provider;

import de.floydkretschmar.fixturize.annotations.FixtureBuilder;
import de.floydkretschmar.fixturize.annotations.FixtureConstructor;

import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import java.util.Arrays;

/**
 * Defines the default behaviour for all constants that are defined as CLASS type kind but do not have a class value
 * provider defined.
 * 1. If type is annotated with @FixtureConstructor or @FixtureBuilder: use first annotation for defining the fallback value
 * 2. "null"
 *
 * @author Floyd Kretschmar
 */
public class ClassValueProvider implements ValueProvider {

    public static final String DEFAULT_VALUE = "null";

    /**
     * Returns the default value for all classes that do not have specific value providers defined
     * @param field - for which the value is being provided
     * @return the default value
     */
    @Override
    public String provideValueAsString(VariableElement field) {
        final var fieldType = field.asType();
        final var declaredElement = ((DeclaredType) fieldType).asElement();

        final var declaredFixtureConstructors = declaredElement.getAnnotationsByType(FixtureConstructor.class);
        if (declaredFixtureConstructors.length > 0)
            return provideConstructorCreationMethodAsValue(declaredElement, declaredFixtureConstructors);

        final var declaredFixtureBuilders = declaredElement.getAnnotationsByType(FixtureBuilder.class);
        if (declaredFixtureBuilders.length > 0)
            return provideBuilderCreationMethodAsValue(declaredElement, declaredFixtureBuilders);

        return DEFAULT_VALUE;
    }

    private static String provideBuilderCreationMethodAsValue(Element declaredElement, FixtureBuilder[] declaredFixtureBuilders) {
        return Arrays.stream(declaredFixtureBuilders)
                .findFirst()
                .map(firstBuilder -> "%sFixture.%s().build()".formatted(declaredElement.asType().toString(), firstBuilder.methodName()))
                .orElse(DEFAULT_VALUE);
    }

    private static String provideConstructorCreationMethodAsValue(Element declaredElement, FixtureConstructor[] declaredFixtureConstructors) {
        return Arrays.stream(declaredFixtureConstructors)
                .findFirst()
                .map(firstBuilder -> "%sFixture.%s()".formatted(declaredElement.asType().toString(), firstBuilder.methodName()))
                .orElse(DEFAULT_VALUE);
    }
}
