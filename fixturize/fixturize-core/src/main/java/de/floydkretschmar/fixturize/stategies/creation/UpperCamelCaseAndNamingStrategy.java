package de.floydkretschmar.fixturize.stategies.creation;

import com.google.common.base.CaseFormat;
import de.floydkretschmar.fixturize.domain.FixtureConstantDefinition;

import java.util.Collection;
import java.util.stream.Collectors;

public class UpperCamelCaseAndNamingStrategy implements CreationMethodNamingStrategy {
    @Override
    public String createMethodName(String className, Collection<FixtureConstantDefinition> usedFields) {
        final var parameterPart = usedFields.stream()
                .map(constant -> CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, constant.getOriginalFieldName()))
                .collect(Collectors.joining("And"));
        return "create%sFixtureWith%s".formatted(className, parameterPart);
    }
}
