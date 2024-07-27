package de.floydkretschmar.fixturize.stategies.value.providers.fallback;

import de.floydkretschmar.fixturize.annotations.FixtureBuilder;
import de.floydkretschmar.fixturize.annotations.FixtureConstructor;
import de.floydkretschmar.fixturize.stategies.metadata.TypeMetadata;
import de.floydkretschmar.fixturize.stategies.value.providers.FallbackValueProvider;
import de.floydkretschmar.fixturize.stategies.value.providers.ValueProvider;
import lombok.RequiredArgsConstructor;

import javax.lang.model.element.Element;
import javax.lang.model.type.DeclaredType;
import java.util.Arrays;

import static java.util.Comparator.comparing;
import static javax.lang.model.element.ElementKind.CLASS;
import static javax.lang.model.type.TypeKind.DECLARED;


/**
 * Defines the default fallback strategy for getting values where no value provider is defined:
 * 1. If type is annotated with {@link FixtureConstructor} or {@link FixtureBuilder}: use the annotation with the most
 * {@link FixtureConstructor#constructorParameters()} or {@link FixtureBuilder#usedSetters()} and use the creation method
 * defined by that annotation
 * 2. If any lombok annotations are present, try to generate to correct value according to the annotation:
 * <ul>
 *     <li>{@link lombok.Builder}: create an inline builder for all fields</li>
 *     <li>{@link lombok.AllArgsConstructor}: create an inline new call to the constructor using all fields</li>
 *     <li>{@link lombok.RequiredArgsConstructor}: create an inline new call to the constructor using all fields that are final
 *     and don't have a constant value</li>
 *     <li>{@link lombok.AllArgsConstructor}: create an inline new call to the constructor without arguments</li>
 * </ul>
 * 3. If any manually generated constructors are present, use the one with the most arguments as the value
 * 4. If any manually written builder methods are present, create an inline builder call for all setter that match the
 * fields of the constant type. Matching in this case means that the setter method name must end on the name of the field.
 */
@RequiredArgsConstructor
public class ClassValueProvider implements FallbackValueProvider {
    private final ValueProvider builderValueProvider;

    private final ValueProvider constructorValueProvider;

    @Override
    public boolean canProvideFallback(Element element, TypeMetadata metadata) {
        final var type = element.asType();
        if (type.getKind() == DECLARED && type instanceof DeclaredType declaredType) {
            final var declaredElement = declaredType.asElement();
            return declaredElement.getKind() == CLASS;
        }

        return false;
    }

    @Override
    public String provideValueAsString(Element field, TypeMetadata metadata) {
        final var fieldType = ((DeclaredType) field.asType());
        var returnValue = provideBuilderCreationMethodAsValue(fieldType.asElement(), metadata);
        if (!returnValue.equals(DEFAULT_VALUE)) return returnValue;

        returnValue = this.builderValueProvider.provideValueAsString(field, metadata);
        if (!returnValue.equals(DEFAULT_VALUE)) return returnValue;

        returnValue = provideConstructorCreationMethodAsValue(fieldType.asElement(), metadata);
        if (!returnValue.equals(DEFAULT_VALUE)) return returnValue;

        return this.constructorValueProvider.provideValueAsString(field, metadata);
    }

    private static String provideBuilderCreationMethodAsValue(Element declaredElement, TypeMetadata metadata) {
        return Arrays.stream(declaredElement.getAnnotationsByType(FixtureBuilder.class))
                .max(comparing(annotation -> annotation.usedSetters().length))
                .map(firstBuilder -> "%sFixture.%s()".formatted(metadata.getQualifiedClassNameWithoutGeneric(), firstBuilder.methodName()))
                .orElse(DEFAULT_VALUE);
    }

    private static String provideConstructorCreationMethodAsValue(Element declaredElement, TypeMetadata metadata) {
        return Arrays.stream(declaredElement.getAnnotationsByType(FixtureConstructor.class))
                .max(comparing(annotation -> annotation.constructorParameters().length))
                .map(firstBuilder -> "%sFixture.%s()".formatted(metadata.getQualifiedClassNameWithoutGeneric(), firstBuilder.methodName()))
                .orElse(DEFAULT_VALUE);
    }
}
