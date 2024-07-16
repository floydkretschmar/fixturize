package de.floydkretschmar.fixturize.stategies.constants;

import com.google.common.base.CaseFormat;
import de.floydkretschmar.fixturize.exceptions.FixtureCreationException;

import java.util.regex.Pattern;

/***
 * Creates a constant name by transforming the camel case field name into screaming snake case.
 */
public class CamelCaseToScreamingSnakeCaseNamingStrategy implements ConstantsNamingStrategy {
    /**
     * Returns a screaming snake case representation of the provided field name.
     *
     * @param fieldName - that is used to create the constant name
     * @return the constant nane
     * @throws FixtureCreationException if the provided field name is not camel case
     */
    @Override
    public String createConstantName(String fieldName) {
        final var pattern = Pattern.compile("^[a-z]+([A-Z][a-z0-9]+)+");
        final var matcher = pattern.matcher(fieldName);
        if (!matcher.matches())
            throw new FixtureCreationException("The field name %s does not match the expected camel case format for a field.".formatted(fieldName));
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, fieldName);
    }
}
