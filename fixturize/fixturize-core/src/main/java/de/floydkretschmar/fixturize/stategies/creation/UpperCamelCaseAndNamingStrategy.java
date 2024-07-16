package de.floydkretschmar.fixturize.stategies.creation;

import com.google.common.base.CaseFormat;
import de.floydkretschmar.fixturize.domain.Constant;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Defines a method naming strategy that creates the following creation method name
 * <br><br>
 * create<b>className</b>FixtureWith<b>usedConstant1</b>And<b>usedConstant2</b>And...<b>usedConstantN</b>
 * <br><br>
 * where for each <b>usedConstant</b> {@link Constant#getOriginalFieldName()} is getting transformed to upper camel case.
 */
public class UpperCamelCaseAndNamingStrategy implements CreationMethodNamingStrategy {
    @Override
    public String createMethodName(String className, Collection<Constant> usedConstants) {
        final var parameterPart = usedConstants.stream()
                .map(constant -> CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, constant.getOriginalFieldName()))
                .collect(Collectors.joining("And"));
        return "create%sFixtureWith%s".formatted(className, parameterPart);
    }
}
