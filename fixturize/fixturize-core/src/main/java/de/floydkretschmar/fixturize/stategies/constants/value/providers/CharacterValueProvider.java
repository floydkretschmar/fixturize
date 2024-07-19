package de.floydkretschmar.fixturize.stategies.constants.value.providers;

import de.floydkretschmar.fixturize.domain.Names;

import javax.lang.model.element.VariableElement;

/**
 * Default value provider for {@link Character} and {@link char}.
 *
 * @author Floyd Kretschmar
 */
public class CharacterValueProvider implements ValueProvider {
    @Override
    public String provideValueAsString(VariableElement field, Names names) {
        return "' '";
    }
}
