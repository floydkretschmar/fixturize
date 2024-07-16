package de.floydkretschmar.fixturize.stategies.constants.value.provider;

import com.google.common.base.CaseFormat;

import javax.lang.model.element.VariableElement;

/***
 * Default value provider for {@link String}.
 *
 * @author Floyd Kretschmar
 */
public class StringValueProvider implements ValueProvider {

    /**
     * Returns the default value to use for constants of type {@link String}. The default value for {@link String} constants
     * is the name of the field in screaming snake case with "_VALUE" appended at the end.
     *
     * @param field - for which the value should be provided
     * @return the text representation of the constant value
     */
    @Override
    public String provideValueAsString(VariableElement field) {
        return "\"%s_VALUE\"".formatted(CaseFormat.LOWER_CAMEL.to(
                CaseFormat.UPPER_UNDERSCORE, field.getSimpleName().toString()));
    }
}
