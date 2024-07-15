package de.floydkretschmar.fixturize.stategies.constants;

import de.floydkretschmar.fixturize.annotations.FixtureConstant;
import de.floydkretschmar.fixturize.annotations.FixtureConstants;
import de.floydkretschmar.fixturize.domain.FixtureConstantDefinition;
import de.floydkretschmar.fixturize.stategies.constants.value.ValueProviderService;

import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.ElementFilter;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ConstantGenerationStrategy {
    private final ConstantsNamingStrategy constantsNamingStrategy;
    private final ValueProviderService valueProviderService;

    public ConstantGenerationStrategy(ConstantsNamingStrategy constantsNamingStrategy, ValueProviderService valueProviderService) {
        this.constantsNamingStrategy = constantsNamingStrategy;
        this.valueProviderService = valueProviderService;
    }

    public ConstantDefinitionMap generateConstants(TypeElement element) {
        final var fields = ElementFilter.fieldsIn(element.getEnclosedElements());
        final var linkedHashMap = createFixtureConstants(fields.stream())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (u, v) -> {
                            throw new IllegalStateException(String.format("Duplicate key %s", u));
                        },
                        LinkedHashMap::new));
        return new FixtureConstantDefinitionMap(linkedHashMap);
    }

    private Stream<Map.Entry<String, FixtureConstantDefinition>> createFixtureConstants(Stream<VariableElement> fields) {
        return fields.flatMap(field -> {
            final var constantsAnnotation = field.getAnnotation(FixtureConstants.class);

            if (Objects.isNull(constantsAnnotation)) {
                final var constantAnnotation = field.getAnnotation(FixtureConstant.class);
                final var constantDefinition = createConstantDefinition(constantAnnotation, field);
                final var key = Objects.nonNull(constantAnnotation) ? constantDefinition.getName() : field.getSimpleName().toString();
                return Stream.of(Map.entry(key, constantDefinition));
            }

            return Arrays.stream(constantsAnnotation.value()).map(constantAnnotation -> {
                final var constantDefinition = createConstantDefinition(constantAnnotation, field);
                return Map.entry(constantDefinition.getName(), constantDefinition);
            });
        });
    }

    private FixtureConstantDefinition createConstantDefinition(FixtureConstant constantAnnotation, VariableElement field) {
        final var originalFieldName = field.getSimpleName().toString();
        final var constantDefinitionBuilder = FixtureConstantDefinition.builder().type(field.asType().toString());
        if (Objects.nonNull(constantAnnotation)) {
            constantDefinitionBuilder.name(constantAnnotation.name());
        } else {
            final var constantName = constantsNamingStrategy.rename(originalFieldName);
            constantDefinitionBuilder.name(constantName);
        }

        if (Objects.nonNull(constantAnnotation) && !constantAnnotation.value().isEmpty()) {
            constantDefinitionBuilder.value(constantAnnotation.value());
        } else {
            final var constantValue = this.valueProviderService.getValueFor(field);
            constantDefinitionBuilder.value(constantValue);
        }

        constantDefinitionBuilder.originalFieldName(originalFieldName);
        return constantDefinitionBuilder.build();
    }
}
