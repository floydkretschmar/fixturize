import com.google.common.base.Function;
import stategies.constants.ConstantsGenerationStrategy;
import stategies.constants.ConstantsNamingStrategy;
import stategies.creation.CreationMethodGenerationStrategy;
import stategies.creation.FixtureConstructorCreationMethodGenerationStrategy;
import stategies.constants.DefaultConstantGenerationStrategy;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FixtureCreator<T> {
    private final ConstantsGenerationStrategy constantsGenerationStrategy;
    private final List<CreationMethodGenerationStrategy> creationMethodStrategies;

    public FixtureCreator(
            Map<Class<?>, Function<Field, String>> customValueProviders,
            List<CreationMethodGenerationStrategy> customCreationMethodStrategies,
            ConstantsNamingStrategy constantsNamingStrategy) {
        this.creationMethodStrategies = new ArrayList<>(customCreationMethodStrategies);
        this.constantsGenerationStrategy = new DefaultConstantGenerationStrategy(constantsNamingStrategy, customValueProviders);

        if (this.creationMethodStrategies.stream()
                .noneMatch(strategy -> strategy.getClass().equals(FixtureConstructorCreationMethodGenerationStrategy.class)))
            this.creationMethodStrategies.add(new FixtureConstructorCreationMethodGenerationStrategy(constantsNamingStrategy));
    }

    public String createFixtureForClass(Class<T> targetClass) {
        final String packageName = targetClass.getPackageName();

        final String fixtureClassTemplate = """
                %spublic class %sFixture {
                %s
                
                %s
                }
                """;

        final String packageString = packageName.isEmpty() ? "" : "package %s;\n\n".formatted(packageName);
        final String constantsString = String.join("\n", this.constantsGenerationStrategy.generateConstants(targetClass));
        final String creationMethodsString = this.creationMethodStrategies.stream().flatMap(stategy -> stategy.generateCreationMethods(targetClass).stream()).collect(Collectors.joining("\n\n"));

        return String.format(fixtureClassTemplate, packageString, targetClass.getSimpleName(), constantsString, creationMethodsString);
    }
}
