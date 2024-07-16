package de.floydkretschmar.fixturize.stategies.creation;

import de.floydkretschmar.fixturize.annotations.FixtureBuilder;
import de.floydkretschmar.fixturize.domain.FixtureConstantDefinition;
import de.floydkretschmar.fixturize.domain.FixtureCreationMethodDefinition;
import de.floydkretschmar.fixturize.stategies.constants.ConstantDefinitionMap;

import javax.lang.model.element.TypeElement;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import static de.floydkretschmar.fixturize.FormattingUtils.WHITESPACE_16;

public class FixtureBuilderStrategy implements CreationMethodGenerationStrategy {
    private final CreationMethodNamingStrategy namingStrategy;

    public FixtureBuilderStrategy(CreationMethodNamingStrategy namingStrategy) {
        this.namingStrategy = namingStrategy;
    }

    @Override
    public Collection<FixtureCreationMethodDefinition> generateCreationMethods(TypeElement element, ConstantDefinitionMap constantMap) {
        return Arrays.stream(element.getAnnotationsByType(FixtureBuilder.class))
                .map(annotation -> {
                    Collection<FixtureConstantDefinition> correspondingConstants;
                    if (annotation.usedSetters().length == 0)
                        correspondingConstants = constantMap.values().stream().toList();
                    else
                        correspondingConstants = constantMap.getMatchingConstants(Arrays.asList(annotation.usedSetters()));

                    final var className = element.getSimpleName().toString();

                    return FixtureCreationMethodDefinition.builder()
                            .returnType("%s.%sBuilder".formatted(className, className))
                            .returnValue(createReturnValueString(className, annotation.builderMethod(), correspondingConstants))
                            .name(this.namingStrategy.createMethodName("%sBuilder".formatted(className), correspondingConstants))
                            .build();
                }).toList();
    }

    private static String createReturnValueString(String className, String buildMethod, Collection<FixtureConstantDefinition> correspondingConstants) {
        final var setterString = correspondingConstants.stream()
                .map(constant -> ".%s(%s)".formatted(constant.getOriginalFieldName(), constant.getName()))
                .collect(Collectors.joining("\n%s".formatted(WHITESPACE_16)));
        return "%s.%s()\n%s%s".formatted(className, buildMethod, WHITESPACE_16, setterString);
    }
}