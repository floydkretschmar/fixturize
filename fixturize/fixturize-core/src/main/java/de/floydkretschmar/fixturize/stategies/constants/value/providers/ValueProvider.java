package de.floydkretschmar.fixturize.stategies.constants.value.providers;

import de.floydkretschmar.fixturize.domain.Metadata;

import javax.lang.model.element.Element;

/**
 * Defines a function that provides a string representation of a value used to construct a constant using a provided field.
 *
 * @author Floyd Kretschmar
 */
@FunctionalInterface
public interface ValueProvider {
    /**
     * The default constant value if all other strategies for generation fail.
     */
    String DEFAULT_VALUE = "null";

    /**
     * Returns a string representation of a value that will be used when creating a constant for the specified field.
     *
     * @param field - for which the value is being provided
     * @param metadata - used for creating the fixture
     * @return the string representation of the value
     */
    String provideValueAsString(Element field, Metadata metadata);
}
