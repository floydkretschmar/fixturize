package de.floydkretschmar.fixturize.stategies.constants.value.provider;

import javax.lang.model.element.VariableElement;
import java.util.UUID;

/***
 * Default value provider for {@link UUID}.
 *
 * @author Floyd Kretschmar
 */
public class UUIDValueProvider implements ValueProvider {

    /**
     * Returns the default value to use for constants of type {@link UUID}. The default value for {@link UUID} constants
     * is "java.util.UUID.fromString(<b>uuid</b>) where <b>uuid</b> is a randomly generated UUID.
     *
     * @param field - for which the value should be provided
     * @return the text representation of the constant value
     */
    @Override
    public String provideValueAsString(VariableElement field) {
        return "java.util.UUID.fromString(\"%s\")".formatted(UUID.randomUUID().toString());
    }
}
