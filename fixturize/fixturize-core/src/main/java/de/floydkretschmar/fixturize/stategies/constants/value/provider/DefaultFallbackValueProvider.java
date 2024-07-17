package de.floydkretschmar.fixturize.stategies.constants.value.provider;

import de.floydkretschmar.fixturize.annotations.FixtureBuilder;
import de.floydkretschmar.fixturize.annotations.FixtureConstructor;

import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import java.util.Arrays;

public class DefaultFallbackValueProvider implements ValueProvider {

    public static final String DEFAULT_VALUE = "null";

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
