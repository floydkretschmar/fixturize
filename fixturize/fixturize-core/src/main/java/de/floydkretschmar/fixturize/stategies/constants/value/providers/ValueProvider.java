package de.floydkretschmar.fixturize.stategies.constants.value.providers;

import de.floydkretschmar.fixturize.domain.Names;

import javax.lang.model.element.VariableElement;

/**
 * Defines a function that provides a string representation of a value used to construct a constant using a provided field.
 *
 * @author Floyd Kretschmar
 */
@FunctionalInterface
public interface ValueProvider {

    /**
     * Returns a string representation of a value that will be used when creating a constant for the specified field.
     *
     * @param field - for which the value is being provided
     * @return the string representation of the value
     */
    String provideValueAsString(VariableElement field, Names name);
}
