package stategies.constants;

import com.google.common.base.CaseFormat;
import com.google.common.base.Defaults;
import com.google.common.base.Function;
import domain.FixtureConstant;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DefaultConstantGenerationStrategy implements ConstantsGenerationStrategy {
    private final ConstantsNamingStrategy constantsNamingStrategy;
    private final Map<Class<?>, Function<Field, String>> valueProviders;

    public DefaultConstantGenerationStrategy(ConstantsNamingStrategy constantsNamingStrategy, Map<Class<?>, Function<Field, String>> valueProviders) {
        this.constantsNamingStrategy = constantsNamingStrategy;
        this.valueProviders = new HashMap<>(valueProviders);

        this.valueProviders.putIfAbsent(String.class, field -> "\"%s_VALUE\"".formatted(CaseFormat.LOWER_CAMEL.to(
                CaseFormat.UPPER_UNDERSCORE, field.getName())));
        this.valueProviders.putIfAbsent(UUID.class, field -> "UUID.randomUUID()");
    }

    @Override
    public <T> Collection<String> generateConstants(Class<T> targetClass) {
        final Stream<FixtureConstant> constants = createFixtureConstants(Arrays.stream(targetClass.getDeclaredFields()));
        return getConstantsStrings(constants);
    }

    private Stream<FixtureConstant> createFixtureConstants(Stream<Field> fields) {
        return fields.map(field -> {
            final String constantName = constantsNamingStrategy.rename(field.getName());
            Class<?> fieldType = field.getType();
            final var constantValue = this.valueProviders.containsKey(fieldType) ?
                    this.valueProviders.get(fieldType).apply(field) : Defaults.defaultValue(fieldType);
            return FixtureConstant.builder().field(field).value(constantValue).name(constantName).build();
        });
    }

    private Collection<String> getConstantsStrings(Stream<FixtureConstant> constants) {
        return constants
                .sorted(Comparator.comparing(FixtureConstant::getName))
                .map(FixtureConstant::toString)
                .toList();
    }
}
