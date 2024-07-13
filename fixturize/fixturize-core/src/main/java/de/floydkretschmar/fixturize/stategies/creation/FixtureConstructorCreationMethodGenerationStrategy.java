package de.floydkretschmar.fixturize.stategies.creation;

import de.floydkretschmar.fixturize.annotations.FixtureConstructors;
import com.google.common.base.CaseFormat;
import de.floydkretschmar.fixturize.domain.FixtureCreationMethod;
import de.floydkretschmar.fixturize.stategies.constants.ConstantsNamingStrategy;

import javax.lang.model.element.Element;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class FixtureConstructorCreationMethodGenerationStrategy implements CreationMethodGenerationStrategy {
    private final ConstantsNamingStrategy constantsNamingStrategy;

    public FixtureConstructorCreationMethodGenerationStrategy(ConstantsNamingStrategy constantsNamingStrategy) {
        this.constantsNamingStrategy = constantsNamingStrategy;
    }

    @Override
    public Collection<FixtureCreationMethod> generateCreationMethods(Element element) {
        return Arrays.stream(element.getAnnotation(FixtureConstructors.class).value())
                .map(constructorAnnotation -> Arrays.asList(constructorAnnotation.parameterNames()))
                .map(paramterNames -> {
                    final String functionName = paramterNames.stream().map(name -> CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, name)).collect(Collectors.joining("And"));
                    final String parameterString = paramterNames.stream().map(constantsNamingStrategy::rename).collect(Collectors.joining(","));
                    final String className = element.getSimpleName().toString();
                    return FixtureCreationMethod.builder()
                            .returnType(className)
                            .returnValue("new %s(%s)".formatted(className, parameterString))
                            .name("create%sFixtureWith%s".formatted(className, functionName))
                            .build();
                }).toList();
    }
}
