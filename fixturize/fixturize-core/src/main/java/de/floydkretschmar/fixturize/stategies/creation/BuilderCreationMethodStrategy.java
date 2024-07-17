package de.floydkretschmar.fixturize.stategies.creation;

import de.floydkretschmar.fixturize.annotations.Fixture;
import de.floydkretschmar.fixturize.annotations.FixtureBuilder;
import de.floydkretschmar.fixturize.domain.Constant;
import de.floydkretschmar.fixturize.domain.CreationMethod;
import de.floydkretschmar.fixturize.stategies.constants.ConstantDefinitionMap;

import javax.lang.model.element.TypeElement;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import static de.floydkretschmar.fixturize.FormattingUtils.WHITESPACE_16;

/**
 * The strategy that generates on creation method for each {@link FixtureBuilder} annotation on a class also annotated
 * with {@link Fixture}. If {@link FixtureBuilder#usedSetters()} is empty, all fields will be used.
 */
public class BuilderCreationMethodStrategy implements CreationMethodGenerationStrategy {
    /**
     * Returns a {@link Collection} of all {@link CreationMethod}s that have been generated
     * for the provided element and constants according to the specified {@link FixtureBuilder} strategy.
     *
     * @param element     - for which the creation methods are being generated
     * @param constantMap - which contains the already generated constants for reference
     * @return a {@link Collection} of generated {@link CreationMethod}s
     */
    @Override
    public Collection<CreationMethod> generateCreationMethods(TypeElement element, ConstantDefinitionMap constantMap) {
        return Arrays.stream(element.getAnnotationsByType(FixtureBuilder.class))
                .map(annotation -> {
                    Collection<Constant> correspondingConstants;
                    if (annotation.usedSetters().length == 0)
                        correspondingConstants = constantMap.values().stream().toList();
                    else
                        correspondingConstants = constantMap.getMatchingConstants(Arrays.asList(annotation.usedSetters()));

                    final var className = element.getSimpleName().toString();

                    return CreationMethod.builder()
                            .returnType("%s.%sBuilder".formatted(className, className))
                            .returnValue(createReturnValueString(className, annotation.builderMethod(), correspondingConstants))
                            .name(annotation.methodName())
                            .build();
                }).toList();
    }

    private static String createReturnValueString(String className, String buildMethod, Collection<Constant> correspondingConstants) {
        final var setterString = correspondingConstants.stream()
                .map(constant -> ".%s(%s)".formatted(constant.getOriginalFieldName(), constant.getName()))
                .collect(Collectors.joining("\n%s".formatted(WHITESPACE_16)));
        return "%s.%s()\n%s%s".formatted(className, buildMethod, WHITESPACE_16, setterString);
    }
}