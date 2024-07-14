package de.floydkretschmar.fixturize.stategies.creation;

import com.google.common.base.CaseFormat;
import de.floydkretschmar.fixturize.annotations.FixtureConstructor;
import de.floydkretschmar.fixturize.annotations.FixtureConstructors;
import de.floydkretschmar.fixturize.domain.FixtureConstantDefinition;
import de.floydkretschmar.fixturize.domain.FixtureCreationMethodDefinition;
import de.floydkretschmar.fixturize.stategies.constants.ConstantDefinitionMap;

import javax.lang.model.element.TypeElement;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FixtureConstructorStrategy implements CreationMethodGenerationStrategy {
    @Override
    public Collection<FixtureCreationMethodDefinition> generateCreationMethods(TypeElement element, ConstantDefinitionMap constantMap) {
        final var fixtureConstructors = element.getAnnotation(FixtureConstructors.class);
        final var fixtureConstructor = element.getAnnotation(FixtureConstructor.class);

        if (Objects.isNull(fixtureConstructors) && Objects.isNull(fixtureConstructor))
            return List.of();

        return (Objects.nonNull(fixtureConstructors) ? Arrays.stream(fixtureConstructors.value()) : Stream.of(fixtureConstructor))
                .map(annotation -> {
                    final var correspondingConstants = constantMap.getMatchingConstants(Arrays.asList(annotation.correspondingFields()));
                    final var functionName = correspondingConstants.stream()
                            .map(constant -> CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, constant.getOriginalFieldName()))
                            .collect(Collectors.joining("And"));
                    final var className = element.getSimpleName().toString();

                    return FixtureCreationMethodDefinition.builder()
                            .returnType(className)
                            .returnValue(createReturnValueString(className, correspondingConstants))
                            .name("create%sFixtureWith%s".formatted(className, functionName))
                            .build();
                }).toList();
    }

    private static String createReturnValueString(String className, List<FixtureConstantDefinition> correspondingConstants) {
        final var parameterString = correspondingConstants.stream()
                .map(FixtureConstantDefinition::getName)
                .collect(Collectors.joining(", "));
        return "new %s(%s)".formatted(className, parameterString);
    }
}
