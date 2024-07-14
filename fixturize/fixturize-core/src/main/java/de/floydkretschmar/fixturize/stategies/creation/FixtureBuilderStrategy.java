package de.floydkretschmar.fixturize.stategies.creation;

import com.google.common.base.CaseFormat;
import de.floydkretschmar.fixturize.annotations.FixtureBuilder;
import de.floydkretschmar.fixturize.annotations.FixtureBuilders;
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

import static de.floydkretschmar.fixturize.FormattingUtils.WHITESPACE_16;

public class FixtureBuilderStrategy implements CreationMethodGenerationStrategy {
    @Override
    public Collection<FixtureCreationMethodDefinition> generateCreationMethods(TypeElement element, ConstantDefinitionMap constantMap) {
        final var builderAnnotationContainer = element.getAnnotation(FixtureBuilders.class);
        final var builderAnnotation = element.getAnnotation(FixtureBuilder.class);

        if (Objects.isNull(builderAnnotationContainer) && Objects.isNull(builderAnnotation))
            return List.of();

        return (Objects.nonNull(builderAnnotationContainer) ? Arrays.stream(builderAnnotationContainer.value()) : Stream.of(builderAnnotation))
                .map(annotation -> {
                    final var correspondingConstants = constantMap.getMatchingConstants(Arrays.asList(annotation.correspondingFields()));
                    final var functionName = correspondingConstants.stream()
                            .map(constant -> CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, constant.getOriginalFieldName()))
                            .collect(Collectors.joining("And"));

                    final var className = element.getSimpleName().toString();
                    final var builderClassName = "%s.%sBuilder".formatted(className, className);

                    return FixtureCreationMethodDefinition.builder()
                            .returnType(builderClassName)
                            .returnValue(createReturnValueString(className, annotation.buildMethod(), correspondingConstants))
                            .name("create%sFixtureBuilderWith%s".formatted(className, functionName))
                            .build();
                }).toList();
    }

    private static String createReturnValueString(String className, String buildMethod, List<FixtureConstantDefinition> correspondingConstants) {
        final var setterString = correspondingConstants.stream()
                .map(constant -> ".%s(%s)".formatted(constant.getOriginalFieldName(), constant.getName()))
                .collect(Collectors.joining("\n%s".formatted(WHITESPACE_16)));
        return "%s.%s()\n%s%s".formatted(className, buildMethod, WHITESPACE_16, setterString);
    }
}