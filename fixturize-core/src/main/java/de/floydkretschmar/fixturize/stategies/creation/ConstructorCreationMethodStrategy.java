package de.floydkretschmar.fixturize.stategies.creation;

import de.floydkretschmar.fixturize.annotations.Fixture;
import de.floydkretschmar.fixturize.annotations.FixtureConstant;
import de.floydkretschmar.fixturize.annotations.FixtureConstructor;
import de.floydkretschmar.fixturize.domain.Constant;
import de.floydkretschmar.fixturize.domain.CreationMethod;
import de.floydkretschmar.fixturize.domain.TypeMetadata;
import de.floydkretschmar.fixturize.stategies.constants.ConstantMap;
import de.floydkretschmar.fixturize.stategies.constants.value.ValueProviderService;
import lombok.RequiredArgsConstructor;

import javax.lang.model.element.TypeElement;
import java.util.Arrays;
import java.util.Collection;

/**
 * The strategy that generates on creation method for each {@link FixtureConstructor} annotation on a class also annotated
 * with {@link Fixture}. The parameter names specified in {@link FixtureConstructor#constructorParameters()} have to be in
 * the same order in the constructor that is being referenced. The value itself has to be either the name of the
 * corresponding field or {@link FixtureConstant#name()} if specified on the corresponding field..
 */
@RequiredArgsConstructor
public class ConstructorCreationMethodStrategy implements CreationMethodGenerationStrategy {

    private final ValueProviderService valueProviderService;
    /**
     * Returns a {@link Collection} of all {@link CreationMethod}s that have been generated
     * for the provided element and constants according to the specified {@link FixtureConstructor} strategy.
     *
     * @param element     - for which the creation methods are being generated
     * @param constantMap - which contains the already generated constants for reference
     * @return a {@link Collection} of generated {@link CreationMethod}s
     */
    @Override
    public Collection<CreationMethod> generateCreationMethods(TypeElement element, ConstantMap constantMap, TypeMetadata metadata) {
        return Arrays.stream(element.getAnnotationsByType(FixtureConstructor.class))
                .map(annotation -> {
                    final var parameterToConstant = constantMap.getMatchingConstants(Arrays.asList(annotation.constructorParameters()));
                    final var className = "%s%s".formatted(metadata.getSimpleClassNameWithoutGeneric(), metadata.isGeneric() ? "<>" : "");
                    final var parameterValues = parameterToConstant.entrySet().stream().map(parameterAndOptionalConstant -> {
                        final var value = parameterAndOptionalConstant.getValue()
                                .map(Constant::getName)
                                .orElse(valueProviderService.resolveValuesForDefaultPlaceholders(parameterAndOptionalConstant.getKey()));
                        return value;
                    }).toList();

                    return CreationMethod.builder()
                            .returnType(metadata.getSimpleClassName())
                            .returnValue(createReturnValueString(className, parameterValues))
                            .name(annotation.methodName())
                            .build();
                }).toList();
    }

    private static String createReturnValueString(String className, Collection<String> correspondingConstants) {
        final var parameterString = String.join(", ", correspondingConstants);
        return "new %s(%s)".formatted(className, parameterString);
    }
}
