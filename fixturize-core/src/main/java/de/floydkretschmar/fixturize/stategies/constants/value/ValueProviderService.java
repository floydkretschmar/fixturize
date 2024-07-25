package de.floydkretschmar.fixturize.stategies.constants.value;

import javax.lang.model.element.Element;

/**
 * Defines methods to retrieve the corresponding value representation for a given {@link Element}.
 *
 * @author Floyd Kretschmar
 */
public interface ValueProviderService {

    /**
     * Returns the correct corresponding value representation for the specified field.
     *
     * @param field - for which the value is being retrieved
     * @return the correct value representation
     */
    String getValueFor(Element field);

    /**
     * Returns the resolved value representation with the correct values for all default value wildcards that were present in the
     * provided value string.
     * @param valueStringWithPlaceholders - that contains 0 to N default value wildcards
     * @return the fully resolved value
     */
    String resolveValuesForDefaultPlaceholders(String valueStringWithPlaceholders);
}
