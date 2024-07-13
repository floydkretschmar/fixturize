package de.floydkretschmar.fixturize.stategies.constants;

import de.floydkretschmar.fixturize.domain.FixtureConstant;
import de.floydkretschmar.fixturize.stategies.constants.value.DefaultValueProviders;
import de.floydkretschmar.fixturize.stategies.constants.value.ValueProvider;

import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.ElementFilter;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Stream;

public class DefaultConstantGenerationStrategy implements ConstantsGenerationStrategy {
    private final ConstantsNamingStrategy constantsNamingStrategy;
    private final DefaultValueProviders valueProviders;

    public DefaultConstantGenerationStrategy(ConstantsNamingStrategy constantsNamingStrategy, Map<String, ValueProvider> customValueProviders) {
        this.constantsNamingStrategy = constantsNamingStrategy;
        this.valueProviders = new DefaultValueProviders(customValueProviders);
    }

    @Override
    public Collection<FixtureConstant> generateConstants(Element element) {
        final var fields = ElementFilter.fieldsIn(element.getEnclosedElements());
        return createFixtureConstants(fields.stream()).toList();
    }

    private Stream<FixtureConstant> createFixtureConstants(Stream<VariableElement> fields) {
        return fields.map(field -> {
            final String constantName = constantsNamingStrategy.rename(field.getSimpleName().toString());
            final String fieldType = field.asType().toString();
            final var constantValue = this.valueProviders.containsKey(fieldType) ?
                    this.valueProviders.get(fieldType).provideValueAsString(field) : "null";
            return FixtureConstant.builder().type(fieldType).value(constantValue).name(constantName).build();
        });
    }
}
