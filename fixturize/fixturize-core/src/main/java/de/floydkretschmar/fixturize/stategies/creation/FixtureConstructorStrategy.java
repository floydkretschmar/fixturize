package de.floydkretschmar.fixturize.stategies.creation;

import com.google.common.base.CaseFormat;
import de.floydkretschmar.fixturize.annotations.FixtureConstructor;
import de.floydkretschmar.fixturize.annotations.FixtureConstructors;
import de.floydkretschmar.fixturize.domain.FixtureConstantDefinition;
import de.floydkretschmar.fixturize.domain.FixtureCreationMethodDefinition;
import de.floydkretschmar.fixturize.exceptions.FixtureCreationException;

import javax.lang.model.element.TypeElement;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FixtureConstructorStrategy implements CreationMethodGenerationStrategy {
    public FixtureConstructorStrategy() {
    }

    @Override
    public Collection<FixtureCreationMethodDefinition> generateCreationMethods(TypeElement element, Map<String, FixtureConstantDefinition> constantMap) {
        final var fixtureConstructors = element.getAnnotation(FixtureConstructors.class);
        final var fixtureConstructor = element.getAnnotation(FixtureConstructor.class);

        if (Objects.isNull(fixtureConstructors) && Objects.isNull(fixtureConstructor))
            return List.of();

        return (Objects.nonNull(fixtureConstructors) ? Arrays.stream(fixtureConstructors.value()) : Stream.of(fixtureConstructor))
                .map(constructorAnnotation -> Arrays.asList(constructorAnnotation.correspondingFields()))
                .map(correspondingFieldNames -> {
                    final var correspondingConstants = correspondingFieldNames.stream().map(parameterName -> {
                        if (constantMap.containsKey(parameterName))
                            return constantMap.get(parameterName);

                        throw new FixtureCreationException("The parameter %s specified in @FixtureConstructor has no corresponding field in %s"
                                .formatted(parameterName, element.getSimpleName().toString()));
                    }).toList();
                    final var functionName = correspondingConstants.stream()
                            .map(constant -> CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, constant.getOriginalFieldName()))
                            .collect(Collectors.joining("And"));
                    final var parameterString = correspondingConstants.stream()
                            .map(FixtureConstantDefinition::getName)
                            .collect(Collectors.joining(", "));
                    final var className = element.getSimpleName().toString();
                    return FixtureCreationMethodDefinition.builder()
                            .returnType(className)
                            .returnValue("new %s(%s)".formatted(className, parameterString))
                            .name("create%sFixtureWith%s".formatted(className, functionName))
                            .build();
                }).toList();
    }
}
