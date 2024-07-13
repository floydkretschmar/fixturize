package de.floydkretschmar.fixturize.stategies.creation;

import de.floydkretschmar.fixturize.annotations.FixtureConstructor;
import de.floydkretschmar.fixturize.annotations.FixtureConstructors;
import com.google.common.base.CaseFormat;
import de.floydkretschmar.fixturize.domain.FixtureCreationMethod;
import de.floydkretschmar.fixturize.stategies.constants.ConstantsNamingStrategy;

import javax.lang.model.element.Element;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FixtureConstructorCreationMethodGenerationStrategy extends BaseCreationMethodGenerationStrategy {
    public FixtureConstructorCreationMethodGenerationStrategy(ConstantsNamingStrategy constantsNamingStrategy) {
        super(constantsNamingStrategy);
    }

    @Override
    protected  <T> List<FixtureCreationMethod> createCreationMethods(Element element) {
        return Arrays.stream(element.getAnnotation(FixtureConstructors.class).value())
                .map(constructorAnnotation -> Arrays.asList(constructorAnnotation.parameterNames()))
                .map(paramterNames -> {
                    final String functionName = paramterNames.stream().map(name -> CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, name)).collect(Collectors.joining("And"));
                    final String parameterString = paramterNames.stream().map(constantsNamingStrategy::rename).collect(Collectors.joining(","));
                    final String className = element.getSimpleName().toString();
                    return FixtureCreationMethod.builder()
                            .returnType(className)
                            .creationCallString("new %s(%s)".formatted(className, parameterString))
                            .methodName("create%sFixtureWith%s".formatted(className, functionName))
                            .build();
                }).toList();
    }
}
