package de.floydkretschmar.fixturize.stategies.constants;

import com.google.common.base.CaseFormat;
import com.google.common.base.Defaults;
import com.google.common.base.Function;
import de.floydkretschmar.fixturize.domain.FixtureConstant;

import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public class DefaultConstantGenerationStrategy implements ConstantsGenerationStrategy {
    private final ConstantsNamingStrategy constantsNamingStrategy;
    private final DefaultValueProviders valueProviders;

    public DefaultConstantGenerationStrategy(ConstantsNamingStrategy constantsNamingStrategy, Map<String, Function<VariableElement, String>> customValueProviders) {
        this.constantsNamingStrategy = constantsNamingStrategy;
        this.valueProviders = new DefaultValueProviders(customValueProviders);
    }

    @Override
    public <T> Collection<String> generateConstants(Element element) {
        final var fields = ElementFilter.fieldsIn(element.getEnclosedElements());
        final Stream<FixtureConstant> constants = createFixtureConstants(fields.stream());
        return getConstantsStrings(constants);
    }

    private Stream<FixtureConstant> createFixtureConstants(Stream<VariableElement> fields) {
        return fields.map(field -> {
            final String constantName = constantsNamingStrategy.rename(field.getSimpleName().toString());
            final String fieldType = field.asType().toString();
            final var constantValue = this.valueProviders.containsKey(fieldType) ?
                    this.valueProviders.get(fieldType).apply(field) : "null";
            return FixtureConstant.builder().fieldType(fieldType).value(constantValue).name(constantName).build();
        });
    }

    private Collection<String> getConstantsStrings(Stream<FixtureConstant> constants) {
        return constants
                .sorted(Comparator.comparing(FixtureConstant::getName))
                .map(FixtureConstant::toString)
                .toList();
    }
}
