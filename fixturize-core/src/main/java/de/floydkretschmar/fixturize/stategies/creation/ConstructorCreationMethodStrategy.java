package de.floydkretschmar.fixturize.stategies.creation;

import de.floydkretschmar.fixturize.annotations.Fixture;
import de.floydkretschmar.fixturize.annotations.FixtureConstant;
import de.floydkretschmar.fixturize.annotations.FixtureConstructor;
import de.floydkretschmar.fixturize.domain.Constant;
import de.floydkretschmar.fixturize.domain.CreationMethod;
import de.floydkretschmar.fixturize.domain.TypeMetadata;
import de.floydkretschmar.fixturize.stategies.constants.ConstantDefinitionMap;

import javax.lang.model.element.TypeElement;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * The strategy that generates on creation method for each {@link FixtureConstructor} annotation on a class also annotated
 * with {@link Fixture}. The parameter names specified in {@link FixtureConstructor#constructorParameters()} have to be in
 * the same order in the constructor that is being referenced. The value itself has to be either the name of the
 * corresponding field or {@link FixtureConstant#name()} if specified on the corresponding field..
 */
public class ConstructorCreationMethodStrategy implements CreationMethodGenerationStrategy {
    /**
     * Returns a {@link Collection} of all {@link CreationMethod}s that have been generated
     * for the provided element and constants according to the specified {@link FixtureConstructor} strategy.
     *
     * @param element     - for which the creation methods are being generated
     * @param constantMap - which contains the already generated constants for reference
     * @return a {@link Collection} of generated {@link CreationMethod}s
     */
    @Override
    public Collection<CreationMethod> generateCreationMethods(TypeElement element, ConstantDefinitionMap constantMap, TypeMetadata metadata) {
        return Arrays.stream(element.getAnnotationsByType(FixtureConstructor.class))
                .map(annotation -> {
                    final var correspondingConstants = constantMap.getMatchingConstants(Arrays.asList(annotation.constructorParameters()));
                    final var className = "%s%s".formatted(metadata.getSimpleClassNameWithoutGeneric(), metadata.isGeneric() ? "<>" : "");

                    return CreationMethod.builder()
                            .returnType(metadata.getSimpleClassName())
                            .returnValue(createReturnValueString(className, correspondingConstants))
                            .name(annotation.methodName())
                            .build();
                }).toList();
    }

    private static String createReturnValueString(String className, Collection<Constant> correspondingConstants) {
        final var parameterString = correspondingConstants.stream()
                .map(Constant::getName)
                .collect(Collectors.joining(", "));
        return "new %s(%s)".formatted(className, parameterString);
    }
}
