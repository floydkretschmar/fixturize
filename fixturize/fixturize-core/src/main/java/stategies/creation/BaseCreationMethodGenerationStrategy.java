package stategies.creation;

import domain.FixtureCreationMethod;
import stategies.constants.ConstantsNamingStrategy;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public abstract class BaseCreationMethodGenerationStrategy implements CreationMethodGenerationStrategy {
    protected final ConstantsNamingStrategy constantsNamingStrategy;

    public BaseCreationMethodGenerationStrategy(ConstantsNamingStrategy constantsNamingStrategy) {
        this.constantsNamingStrategy = constantsNamingStrategy;
    }

    @Override
    public <T> Collection<String> generateCreationMethods(Class<T> targetClass) {
        List<FixtureCreationMethod> creationMethods = createCreationMethods(targetClass);
        return getCreationMethodStrings(creationMethods);
    }

    protected abstract  <T> List<FixtureCreationMethod> createCreationMethods(Class<T> targetClass);

    private static Collection<String> getCreationMethodStrings(List<FixtureCreationMethod> creationMethods) {
        return creationMethods.stream()
                .map(creationMethod -> {
                    return """
                        \tpublic %s %s() {
                        \t\treturn %s;
                        \t}""".formatted(creationMethod.getReturnType(), creationMethod.getMethodName(), creationMethod.getCreationCallString());
                }).toList();
    }
}
