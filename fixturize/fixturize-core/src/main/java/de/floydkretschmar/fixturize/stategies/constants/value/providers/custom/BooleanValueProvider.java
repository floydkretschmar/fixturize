package de.floydkretschmar.fixturize.stategies.constants.value.providers.custom;

import de.floydkretschmar.fixturize.domain.Names;
import de.floydkretschmar.fixturize.stategies.constants.value.providers.ValueProvider;

import javax.lang.model.element.VariableElement;

/**
 * Default value provider for {@link Boolean} and {@link boolean}.
 *
 * @author Floyd Kretschmar
 */
public class BooleanValueProvider implements ValueProvider {
    @Override
    public String provideValueAsString(VariableElement field, Names names) {
        return "false";
    }
}
