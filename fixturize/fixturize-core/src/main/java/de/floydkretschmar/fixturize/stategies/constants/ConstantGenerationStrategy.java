package de.floydkretschmar.fixturize.stategies.constants;

import de.floydkretschmar.fixturize.annotations.FixtureConstant;
import de.floydkretschmar.fixturize.domain.FixtureConstantDefinition;
import de.floydkretschmar.fixturize.stategies.constants.value.DefaultValueProviders;
import de.floydkretschmar.fixturize.stategies.constants.value.ValueProvider;

import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.ElementFilter;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ConstantGenerationStrategy {
    private final ConstantsNamingStrategy constantsNamingStrategy;
    private final DefaultValueProviders valueProviders;

    public ConstantGenerationStrategy(ConstantsNamingStrategy constantsNamingStrategy, Map<String, ValueProvider> customValueProviders) {
        this.constantsNamingStrategy = constantsNamingStrategy;
        this.valueProviders = new DefaultValueProviders(customValueProviders);
    }

    public Map<String, FixtureConstantDefinition> generateConstants(TypeElement element) {
        final var fields = ElementFilter.fieldsIn(element.getEnclosedElements());
        return createFixtureConstants(fields.stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private Stream<Map.Entry<String, FixtureConstantDefinition>> createFixtureConstants(Stream<VariableElement> fields) {
        return fields.map(field -> {
            final String fieldType = field.asType().toString();
            final FixtureConstant constantAnnotation = field.getAnnotation(FixtureConstant.class);

            final var constantDefinitionBuilder = FixtureConstantDefinition.builder().type(fieldType);
            if (Objects.nonNull(constantAnnotation)) {
                constantDefinitionBuilder.name(constantAnnotation.name());
            }
            else {
                final String constantName = constantsNamingStrategy.rename(field.getSimpleName().toString());
                constantDefinitionBuilder.name(constantName);
            }

            if (Objects.nonNull(constantAnnotation) && !constantAnnotation.value().isEmpty()) {
                constantDefinitionBuilder.value(constantAnnotation.value());
            }
            else {
                final var constantValue = this.valueProviders.containsKey(fieldType) ?
                        this.valueProviders.get(fieldType).provideValueAsString(field) : "null";
                constantDefinitionBuilder.value(constantValue);
            }

            return Map.entry(field.getSimpleName().toString(), constantDefinitionBuilder.build());
        });
    }
}
