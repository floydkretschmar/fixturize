package de.floydkretschmar.fixturize.stategies.creation;

import de.floydkretschmar.fixturize.domain.FixtureCreationMethod;
import de.floydkretschmar.fixturize.stategies.constants.ConstantsNamingStrategy;

import javax.lang.model.element.Element;
import java.util.Collection;
import java.util.List;

public abstract class BaseCreationMethodGenerationStrategy implements CreationMethodGenerationStrategy {
    protected final ConstantsNamingStrategy constantsNamingStrategy;

    public BaseCreationMethodGenerationStrategy(ConstantsNamingStrategy constantsNamingStrategy) {
        this.constantsNamingStrategy = constantsNamingStrategy;
    }

    @Override
    public <T> Collection<String> generateCreationMethods(Element element) {
        List<FixtureCreationMethod> creationMethods = createCreationMethods(element);
        return getCreationMethodStrings(creationMethods);
    }

    protected abstract  <T> List<FixtureCreationMethod> createCreationMethods(Element element);

    private static Collection<String> getCreationMethodStrings(List<FixtureCreationMethod> creationMethods) {
        return creationMethods.stream()
                .map(creationMethod -> """
                    \tpublic %s %s() {
                    \t\treturn %s;
                    \t}""".formatted(creationMethod.getReturnType(), creationMethod.getMethodName(), creationMethod.getCreationCallString())).toList();
    }
}
