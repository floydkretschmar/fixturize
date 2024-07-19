package de.floydkretschmar.fixturize.stategies.constants.value.providers;

import de.floydkretschmar.fixturize.domain.Names;
import de.floydkretschmar.fixturize.stategies.constants.value.ValueProviderService;

import javax.lang.model.element.VariableElement;

/**
 * Defines a function that provides a string representation of a value used to construct a constant using a provided field,
 * the names related to fixture creation and the value provider service.
 *
 * @author Floyd Kretschmar
 */
@FunctionalInterface
public interface RecursiveValueProvider {
    /**
     * Returns a string representation of a value that will be used when creating a constant for the specified field.
     *
     * @param field - for which the value is being provided
     * @return the string representation of the value
     */
    String recursivelyProvideValue(VariableElement field, Names name, ValueProviderService valueProviderService);
}
