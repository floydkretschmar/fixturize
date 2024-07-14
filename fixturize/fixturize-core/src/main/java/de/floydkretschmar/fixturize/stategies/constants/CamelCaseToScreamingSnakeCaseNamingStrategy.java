package de.floydkretschmar.fixturize.stategies.constants;

import com.google.common.base.CaseFormat;
import de.floydkretschmar.fixturize.exceptions.FixtureCreationException;

import java.util.regex.Pattern;

public class CamelCaseToScreamingSnakeCaseNamingStrategy implements ConstantsNamingStrategy {
    @Override
    public String rename(String fieldName) {
        final var pattern = Pattern.compile("^[a-z]+([A-Z][a-z0-9]+)+");
        final var matcher = pattern.matcher(fieldName);
        if (!matcher.matches())
            throw new FixtureCreationException("The field name %s does not match the expected camel case format for a field.".formatted(fieldName));
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, fieldName);
    }
}
